package com.ironhack.midtermproject1.controller.impl;

import com.ironhack.midtermproject1.controller.interfaces.ITransactionController;
import com.ironhack.midtermproject1.dao.Transaction;
import com.ironhack.midtermproject1.enums.ReturnType;
import com.ironhack.midtermproject1.enums.TransactionType;
import com.ironhack.midtermproject1.repository.TransactionRepository;
import com.ironhack.midtermproject1.service.impl.TransactionService;
import com.sun.istack.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController implements ITransactionController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public Page<Transaction> getTransactions(
            @RequestParam Optional<Integer> page, // allows to divide results into pages
            @RequestParam Optional<String> sortBy // allows to sort the results by any param
    ){
        return transactionRepository.findAll(
                PageRequest.of(
                        page.orElse(0),
                        5,
                        Sort.Direction.ASC, sortBy.orElse("id")
                )
        );
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Transaction getTransactionById(@PathVariable Long id){
        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);
        return optionalTransaction.orElse(null);
    }

    @GetMapping("/SourceAccountId/SourceAccountType")
    @ResponseStatus(HttpStatus.OK)
    public List<Transaction> getTransactionBySourceAccountIdAndSourceAccountType(
            @RequestParam Long id,
            @RequestParam ReturnType returnType) {
        List<Transaction> transactionList = transactionRepository.findBySourceAccountIdAndSourceAccountType(id, returnType);
        return transactionList.isEmpty() ? null : transactionList;
    }

    //allows to write query with any WHERE statements
    @GetMapping("/getByAnyParams")
    @ResponseStatus(HttpStatus.OK)
    public List<Transaction> getTransactionByAnyParameter(
            @Nullable @RequestParam(required = false) Long sourceAccountId,
            @Nullable @RequestParam(required = false) ReturnType sourceAccountType,
            @Nullable @RequestParam(required = false) Long targetAccountId,
            @Nullable @RequestParam(required = false) ReturnType targetAccountType,
            @Nullable @RequestParam(required = false) Long sourceAccountPrimaryOwner,
            @Nullable @RequestParam(required = false) Long targetAccountPrimaryOwner,
            @Nullable @RequestParam(required = false) TransactionType transactionType
    ) {
        return transactionRepository.findByAnyParams(
                sourceAccountId,
                sourceAccountType.toString(),
                targetAccountId,
                targetAccountType.toString(),
                sourceAccountPrimaryOwner,
                targetAccountPrimaryOwner,
                transactionType.toString());
    }

    @PatchMapping("/detectFraud")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void detectFraud(){
        transactionService.fraudDetection();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTransaction(@PathVariable Long id){
        transactionRepository.deleteById(id);
    }
}
