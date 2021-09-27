package com.ironhack.midtermproject1.service.impl;

import com.ironhack.midtermproject1.dao.*;
import com.ironhack.midtermproject1.enums.AccountStatus;
import com.ironhack.midtermproject1.enums.ReturnType;
import com.ironhack.midtermproject1.enums.TransactionType;
import com.ironhack.midtermproject1.repository.*;
import com.ironhack.midtermproject1.service.interfaces.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;

@Service
public class TransactionService implements ITransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CheckingRepository checkingRepository;

    @Autowired
    private StudentCheckingRepository studentCheckingRepository;

    @Autowired
    private SavingRepository savingRepository;

    @Autowired
    private CreditCardRepository creditCardRepository;

    public void saveTransaction(TransactionType transactionType, BigDecimal amount, Long sourceAccountId, ReturnType sourceAccountType, AccountHolder sourceAccountPrimaryOwner, Long targetAccountId, ReturnType targetAccountType, AccountHolder targetAccountPrimaryOwner) {
        Transaction transaction = new Transaction(
                transactionType, amount, sourceAccountId, sourceAccountType, sourceAccountPrimaryOwner,
                targetAccountId, targetAccountType, targetAccountPrimaryOwner);
        transactionRepository.save(transaction);
    }

    public void freezeAccount(Long id, ReturnType accountType) {
        if (accountType == ReturnType.CHECKING) {

            Optional<Checking> optionalChecking = checkingRepository.findById(id);
            if (optionalChecking.isPresent() && optionalChecking.get().getStatus() != AccountStatus.FROZEN) {
                optionalChecking.get().setStatus(AccountStatus.FROZEN);
                checkingRepository.save(optionalChecking.get());
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The checking account with the given id does not exist or it is already frozen");
            }
        } else if (accountType == ReturnType.STUDENT_CHECKING) {

            Optional<StudentChecking> optionalStudentChecking = studentCheckingRepository.findById(id);
            if (optionalStudentChecking.isPresent() && optionalStudentChecking.get().getStatus() != AccountStatus.FROZEN) {
                optionalStudentChecking.get().setStatus(AccountStatus.FROZEN);
                studentCheckingRepository.save(optionalStudentChecking.get());
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The student checking account with the given id does not exist or it is already frozen");
            }
        } else if (accountType == ReturnType.SAVING) {
            Optional<Saving> optionalSaving = savingRepository.findById(id);
            if (optionalSaving.isPresent()) {
                optionalSaving.get().setStatus(AccountStatus.FROZEN);
                savingRepository.save(optionalSaving.get());
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The saving account with the given id does not exist");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no such account type");
        }
    }

    public void fraudDetection() {
        List<Transaction> transactionList = transactionRepository.findFraudAccount();
        for(int i = 0; i <transactionList.size(); i++){
            freezeAccount(transactionList.get(i).getSourceAccountId(), transactionList.get(i).getSourceAccountType());
        }
    }
}
