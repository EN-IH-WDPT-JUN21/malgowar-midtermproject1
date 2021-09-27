package com.ironhack.midtermproject1.controller.interfaces;

import com.ironhack.midtermproject1.dao.Transaction;
import com.ironhack.midtermproject1.enums.ReturnType;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ITransactionController {
    Transaction getTransactionById(Long id);
    Page<Transaction> getTransactions(Optional<Integer> page, Optional<String> sortBy);
    List<Transaction> getTransactionBySourceAccountIdAndSourceAccountType(Long id, ReturnType returnType);
    void deleteTransaction(Long id);
}
