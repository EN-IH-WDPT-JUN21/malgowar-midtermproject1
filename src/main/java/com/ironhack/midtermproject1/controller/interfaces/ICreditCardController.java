package com.ironhack.midtermproject1.controller.interfaces;

import com.ironhack.midtermproject1.dao.Account;
import com.ironhack.midtermproject1.dao.CreditCard;
import com.ironhack.midtermproject1.dao.Money;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ICreditCardController {
    Page<CreditCard> getCreditCards(Optional<Integer> page, Optional<String> sortBy);
    CreditCard getCreditCardById(Long id);
    List<CreditCard> getCreditCardByAnyParameter(BigDecimal maxBalance, BigDecimal minBalance, BigDecimal maxCreditLimit, BigDecimal minCreditLimit, String maxCreationDate, String minCreationDate, String maxAdditionLastInterestDate, String minAdditionLastInterestDate, BigDecimal maxInterestRate, BigDecimal minInterestRate, Long primaryOwner, Long secondaryOwner);
    CreditCard createCreditCard(CreditCard creditCard);
    CreditCard updateCreditCardBalanceByAddingInterests(Long id);
    Account updateBalanceAfterPayment(Long id, Money money, Authentication authentication);
    Account updateBalanceAfterDeposit(Long id, Money money);
    void updateBalanceAfterTransfer(Long sourceAccountId, Long targetAccountId, String targetAccountType, Optional<Long> primaryOwner, Optional<Long> secondaryOwner, Money money);
    void deleteCreditCard( Long id);
    public List<CreditCard> getCreditCardByIdAndLoggedUser(Long id, Authentication authentication);
}
