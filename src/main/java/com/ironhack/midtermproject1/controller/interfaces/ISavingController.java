package com.ironhack.midtermproject1.controller.interfaces;

import com.ironhack.midtermproject1.dao.Account;
import com.ironhack.midtermproject1.dao.Money;
import com.ironhack.midtermproject1.dao.Saving;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface ISavingController {
    Page<Saving> getSavingAccounts(Optional<Integer> page, Optional<String> sortBy);
    Saving getSavingById(Long id);
    Saving updateSavingBalanceByAddingInterests(Long id);
    Account updateBalanceAfterPayment(Long id, Money money, Authentication authentication);
    Account updateBalanceAfterDeposit(Long id, Money money);
    void updateBalanceAfterTransfer(Long sourceAccountId, Long targetAccountId, String targetAccountType, Optional<Long> primaryOwner, Optional<Long> secondaryOwner, Money money);
    void deleteSavingAccount(Long id);
    List<Saving> getSavingByIdAndLoggedUser(Long id,Authentication authentication);
}
