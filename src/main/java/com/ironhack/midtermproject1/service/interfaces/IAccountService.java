package com.ironhack.midtermproject1.service.interfaces;

import com.ironhack.midtermproject1.dao.Account;
import com.ironhack.midtermproject1.dao.Money;
import com.ironhack.midtermproject1.enums.ReturnType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IAccountService {
    List<BigDecimal> subtractFunds(BigDecimal balance, BigDecimal amount, BigDecimal minimumBalance, BigDecimal penaltyFee, BigDecimal creditLimit);
    List<BigDecimal> addFunds(BigDecimal balance, BigDecimal amount);
    Account makePayment(Long id, Money money, ReturnType accountType, Long primaryOwnerId);
    Account depositMoney(Long id, Money money, ReturnType accountType);
    void transferMoney(Long sourceAccountId, Long targetAccountId, Money money, ReturnType sourceAccountType, ReturnType targetAccountType, Optional<Long> primaryOwner, Optional<Long> secondaryOwner);
}
