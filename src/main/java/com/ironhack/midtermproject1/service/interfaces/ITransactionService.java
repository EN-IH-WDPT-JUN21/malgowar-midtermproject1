package com.ironhack.midtermproject1.service.interfaces;

import com.ironhack.midtermproject1.dao.AccountHolder;
import com.ironhack.midtermproject1.enums.ReturnType;
import com.ironhack.midtermproject1.enums.TransactionType;

import java.math.BigDecimal;

public interface ITransactionService {
    void saveTransaction(TransactionType transactionType, BigDecimal amount, Long sourceAccountId, ReturnType sourceAccountType, AccountHolder sourceAccountPrimaryOwner, Long targetAccountId, ReturnType targetAccountType, AccountHolder targetAccountPrimaryOwner);
}
