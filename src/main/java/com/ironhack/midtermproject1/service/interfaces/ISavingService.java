package com.ironhack.midtermproject1.service.interfaces;

import com.ironhack.midtermproject1.dao.Saving;

public interface ISavingService {
    Saving updateSavingBalanceByAddingInterests(Long id);
    Saving createSavingAccount(Saving savingAccount);
}
