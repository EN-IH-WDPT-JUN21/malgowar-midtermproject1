package com.ironhack.midtermproject1.controller.interfaces;

import com.ironhack.midtermproject1.dao.AccountHolder;
import org.springframework.data.domain.Page;
import java.util.Optional;

public interface IAccountHolderController {
    Page<AccountHolder> getAccountHolders(Optional<Integer> page, Optional<String> sortBy);
    AccountHolder getAccountHolderById(Long id);
}
