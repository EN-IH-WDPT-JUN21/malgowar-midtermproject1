package com.ironhack.midtermproject1.service.impl;

import com.ironhack.midtermproject1.dao.*;
import com.ironhack.midtermproject1.enums.ReturnType;
import com.ironhack.midtermproject1.enums.TransactionType;
import com.ironhack.midtermproject1.repository.AccountHolderRepository;
import com.ironhack.midtermproject1.repository.CheckingRepository;
import com.ironhack.midtermproject1.repository.StudentCheckingRepository;
import com.ironhack.midtermproject1.service.interfaces.ICheckingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class CheckingService extends AccountService implements ICheckingService{

    @Autowired
    private CheckingRepository checkingRepository;

    @Autowired
    private StudentCheckingRepository studentCheckingRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private TransactionService transactionService;

    //post
    public Account createCheckingAccount(Checking checkingAccount){
        LocalDate today = LocalDate.now();
        if (checkingAccount.getPrimaryOwner() != null) {
            Optional<AccountHolder> primaryOwner = accountHolderRepository.findById(checkingAccount.getPrimaryOwner().getId());

            if(primaryOwner.isPresent()) {
                long diff = Math.abs(ChronoUnit.YEARS.between(today, primaryOwner.get().getBirthDate()));
                AccountHolder secondaryOwner;

                if(checkingAccount.getSecondaryOwner() != null) {
                    if(accountHolderRepository.findById(checkingAccount.getSecondaryOwner().getId()).isPresent()){
                        secondaryOwner = accountHolderRepository.findById(checkingAccount.getSecondaryOwner().getId()).get();
                    } else {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The Secondary Owner with the given id does not exist");
                    }
                } else {
                    secondaryOwner = null;
                }
                if (diff < 24) { // create student checking account when account holders age is less than 24
                        StudentChecking studentChecking = new StudentChecking(checkingAccount.getBalance(), checkingAccount.getCurrency(),primaryOwner.get(), secondaryOwner,
                                checkingAccount.getCreationDate(), checkingAccount.getSecretKey(), checkingAccount.getStatus());
                        return studentCheckingRepository.save(studentChecking);
                } else {
                    checkingAccount.setPrimaryOwner(primaryOwner.get());
                    checkingAccount.setSecondaryOwner(secondaryOwner);
                    return checkingRepository.save(checkingAccount);
                }
            }else{
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The Primary Owner with the given id does not exist");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account must have Primary Owner");
        }
    }

    //patch
    public void chargeMaintanenceFee(Long id) {
        LocalDateTime today = LocalDateTime.now();
        if (id != null) { // charge fee for checking account with given id
            Optional<Checking> optionalChecking = checkingRepository.findById(id);
            if (optionalChecking.isPresent()) {
                long diff = Math.abs(ChronoUnit.MONTHS.between(today, optionalChecking.get().getChargeFeeLastDate()));
                if (diff > 0) {
                    BigDecimal newBalance = optionalChecking.get().getBalance().subtract(optionalChecking.get().getMonthlyMaintenanceFee().multiply(new BigDecimal(diff)));
                    optionalChecking.get().setBalance(newBalance);
                    optionalChecking.get().setChargeFeeLastDate(today);
                    transactionService.saveTransaction(TransactionType.MAINTENANCE_FEE, optionalChecking.get().getMonthlyMaintenanceFee().multiply(new BigDecimal(diff)), optionalChecking.get().getId(), ReturnType.CHECKING, optionalChecking.get().getPrimaryOwner(), optionalChecking.get().getId(), ReturnType.CHECKING, optionalChecking.get().getPrimaryOwner());
                    checkingRepository.save(optionalChecking.get());
                }
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The checking account with the given id does not exist");
            }

        } else { // charge fee for all checking accounts
            List<Checking> listChecking = checkingRepository.findAll();
            for (Checking checking: listChecking) {
                long diff = Math.abs(ChronoUnit.MONTHS.between(today, checking.getChargeFeeLastDate()));
                if (diff > 0) {
                    BigDecimal newBalance = checking.getBalance().subtract(checking.getMonthlyMaintenanceFee().multiply(new BigDecimal(diff)));
                    checking.setBalance(newBalance);
                    checking.setChargeFeeLastDate(today);
                    transactionService.saveTransaction(TransactionType.MAINTENANCE_FEE, checking.getMonthlyMaintenanceFee().multiply(new BigDecimal(diff)), checking.getId(), ReturnType.CHECKING, checking.getPrimaryOwner(), checking.getId(), ReturnType.CHECKING, checking.getPrimaryOwner());
                    checkingRepository.save(checking);
                }
            }
        }
    }
}
