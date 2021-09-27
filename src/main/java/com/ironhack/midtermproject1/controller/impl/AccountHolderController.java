package com.ironhack.midtermproject1.controller.impl;

import com.ironhack.midtermproject1.controller.interfaces.IAccountHolderController;
import com.ironhack.midtermproject1.dao.AccountHolder;
import com.ironhack.midtermproject1.repository.AccountHolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/accountHolders")
public class AccountHolderController implements IAccountHolderController {

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<AccountHolder> getAccountHolders(
            @RequestParam Optional<Integer> page, // allows to divide results into pages
            @RequestParam Optional<String> sortBy // allows to sort the results by any param
    ){
        return accountHolderRepository.findAll(
                PageRequest.of(
                        page.orElse(0),
                        5,
                        Sort.Direction.ASC, sortBy.orElse("id")
                )
        );
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AccountHolder getAccountHolderById(@PathVariable Long id){
        Optional<AccountHolder> optionalAccountHolder = accountHolderRepository.findById(id);
        return optionalAccountHolder.orElse(null);
    }
}
