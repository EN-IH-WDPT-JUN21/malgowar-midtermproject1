package com.ironhack.midtermproject1.controller.interfaces;

import com.ironhack.midtermproject1.dao.Account;
import com.ironhack.midtermproject1.dao.Checking;
import com.ironhack.midtermproject1.dao.Money;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface ICheckingController {
    Page<Checking> getCheckingAccounts(Optional<Integer> page, Optional<String> sortBy);
    Checking getCheckingAccountById(Long id);
    Account createCheckingAccount(Checking checking);
    Account updateBalanceAfterPayment(Long id, Money money, Authentication authentication);
    Account updateBalanceAfterDeposit(Long id, Money money);
    void chargeMaintanenceFeeForAllOrAnyAccounts(Long id);
    void updateBalanceAfterTransfer(Long sourceAccountId, Long targetAccountId, String targetAccountType, Optional<Long> primaryOwner, Optional<Long> secondaryOwner, Money money);
    void deleteCheckingAccount(Long id);
    List<Checking> getCheckingAccountByIdAndLoggedUser(Long id, Authentication authentication);
}
