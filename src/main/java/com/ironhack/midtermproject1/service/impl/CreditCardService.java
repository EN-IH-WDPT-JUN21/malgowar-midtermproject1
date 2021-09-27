package com.ironhack.midtermproject1.service.impl;

import com.ironhack.midtermproject1.dao.*;
import com.ironhack.midtermproject1.enums.ReturnType;
import com.ironhack.midtermproject1.enums.TransactionType;
import com.ironhack.midtermproject1.repository.AccountHolderRepository;
import com.ironhack.midtermproject1.repository.CreditCardRepository;
import com.ironhack.midtermproject1.service.interfaces.ICreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class CreditCardService extends AccountService implements ICreditCardService {

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private TransactionService transactionService;

    //patch - when the user selects the credit card by id, it is checked whether interest should be added
    public CreditCard updateCreditCardBalanceByAddingInterests(Long id){
        Optional<CreditCard> optionalCreditCard = creditCardRepository.findById(id);

        if(optionalCreditCard.isPresent()){
            LocalDateTime today = LocalDateTime.now();
            long diff = Math.abs(ChronoUnit.MONTHS.between(today, optionalCreditCard.get().getAdditionLastInterestDate()));
            if(diff > 0){
                BigDecimal newBalance = optionalCreditCard.get().getBalance().multiply(optionalCreditCard.get().getInterestRate().divide(new BigDecimal(12)).add(new BigDecimal(1)).pow((int) diff));
                transactionService.saveTransaction(TransactionType.INTEREST, newBalance.subtract(optionalCreditCard.get().getBalance()), optionalCreditCard.get().getId(), ReturnType.CREDIT_CARD, optionalCreditCard.get().getPrimaryOwner(), optionalCreditCard.get().getId(), ReturnType.CREDIT_CARD, optionalCreditCard.get().getPrimaryOwner());
                optionalCreditCard.get().setBalance(newBalance);
                optionalCreditCard.get().setAdditionLastInterestDate(today);

            }
            return creditCardRepository.save(optionalCreditCard.get());
        }else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The credit card with the given id does not exist");
        }
    }

    //post
    public CreditCard createCreditCard(CreditCard creditCard){
        if (creditCard.getPrimaryOwner() != null) {
            Optional<AccountHolder> primaryOwner = accountHolderRepository.findById(creditCard.getPrimaryOwner().getId());
            if (primaryOwner.isPresent()) {
                AccountHolder secondaryOwner;

                if(creditCard.getSecondaryOwner() != null) {
                    if(accountHolderRepository.findById(creditCard.getSecondaryOwner().getId()).isPresent()){
                        secondaryOwner = accountHolderRepository.findById(creditCard.getSecondaryOwner().getId()).get();
                    } else {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The Secondary Owner with the given id does not exist");
                    }
                } else {
                    secondaryOwner = null;
                }
                creditCard.setPrimaryOwner(primaryOwner.get());
                creditCard.setSecondaryOwner(secondaryOwner);
                return creditCardRepository.save(creditCard);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The Primary Owner with the given id does not exist");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Credit card must have Primary Owner");
        }
    }
}
