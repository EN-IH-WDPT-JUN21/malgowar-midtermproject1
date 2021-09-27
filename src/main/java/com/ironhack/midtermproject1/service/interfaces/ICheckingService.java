package com.ironhack.midtermproject1.service.interfaces;

import com.ironhack.midtermproject1.dao.Account;
import com.ironhack.midtermproject1.dao.Checking;

public interface ICheckingService {
    Account createCheckingAccount(Checking checkingAccount);
    void chargeMaintanenceFee(Long id);
}
