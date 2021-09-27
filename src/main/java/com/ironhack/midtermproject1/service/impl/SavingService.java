package com.ironhack.midtermproject1.service.impl;

import com.ironhack.midtermproject1.dao.AccountHolder;
import com.ironhack.midtermproject1.dao.Saving;
import com.ironhack.midtermproject1.enums.ReturnType;
import com.ironhack.midtermproject1.enums.TransactionType;
import com.ironhack.midtermproject1.repository.AccountHolderRepository;
import com.ironhack.midtermproject1.repository.SavingRepository;
import com.ironhack.midtermproject1.service.interfaces.ISavingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class SavingService extends AccountService implements ISavingService {

    @Autowired
    private SavingRepository savingRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private TransactionService transactionService;

    //patch - when the user selects the saving account by id, it is checked whether interest should be added
    public Saving updateSavingBalanceByAddingInterests(Long id){
        Optional<Saving> optionalSaving = savingRepository.findById(id);

        if(optionalSaving.isPresent()){
            LocalDateTime today = LocalDateTime.now();
            long diff = Math.abs(ChronoUnit.YEARS.between(today, optionalSaving.get().getAdditionLastInterestDate()));
            if(diff > 0){
                BigDecimal newBalance = optionalSaving.get().getBalance().multiply(optionalSaving.get().getInterestRate().add(new BigDecimal(1)).pow((int) diff));
                transactionService.saveTransaction(TransactionType.INTEREST, newBalance.subtract(optionalSaving.get().getBalance()), optionalSaving.get().getId(), ReturnType.SAVING, optionalSaving.get().getPrimaryOwner(), optionalSaving.get().getId(), ReturnType.SAVING, optionalSaving.get().getPrimaryOwner());
                optionalSaving.get().setBalance(newBalance);
                optionalSaving.get().setAdditionLastInterestDate(today);
            }
            return savingRepository.save(optionalSaving.get());
        }else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The saving account with the given id does not exist");
        }
    }

    //post
    public Saving createSavingAccount(Saving savingAccount){
        if (savingAccount.getPrimaryOwner() != null) {
            Optional<AccountHolder> primaryOwner = accountHolderRepository.findById(savingAccount.getPrimaryOwner().getId());
            if (primaryOwner.isPresent()) {
                AccountHolder secondaryOwner;

                if(savingAccount.getSecondaryOwner() != null) {
                    if(accountHolderRepository.findById(savingAccount.getSecondaryOwner().getId()).isPresent()){
                        secondaryOwner = accountHolderRepository.findById(savingAccount.getSecondaryOwner().getId()).get();
                    } else {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The Secondary Owner with the given id does not exist");
                    }
                } else {
                    secondaryOwner = null;
                }
                savingAccount.setPrimaryOwner(primaryOwner.get());
                savingAccount.setSecondaryOwner(secondaryOwner);
                return savingRepository.save(savingAccount);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The Primary Owner with the given id does not exist");
            }
        }else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saving account must have Primary Owner");
        }
    }
}
