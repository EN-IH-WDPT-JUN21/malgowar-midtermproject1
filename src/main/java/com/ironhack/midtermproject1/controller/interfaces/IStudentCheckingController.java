package com.ironhack.midtermproject1.controller.interfaces;

import com.ironhack.midtermproject1.dao.Account;
import com.ironhack.midtermproject1.dao.Money;
import com.ironhack.midtermproject1.dao.StudentChecking;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface IStudentCheckingController {
    Page<StudentChecking> getStudentCheckingAccounts(Optional<Integer> page, Optional<String> sortBy);
    StudentChecking getStudentCheckingAccountById(Long id);
    Account createStudentCheckingAccount(StudentChecking studentChecking);
    Account updateBalanceAfterPayment(Long id, Money money, Authentication authentication);
    Account updateBalanceAfterDeposit(Long id, Money money);
    void updateBalanceAfterTransfer(Long sourceAccountId, Long targetAccountId, String targetAccountType, Optional<Long> primaryOwner, Optional<Long> secondaryOwner, Money money);
    void deleteStudentCheckingAccount(Long id);
    List<StudentChecking> getStudentCheckingAccountByIdAndLoggedUser(Long id, Authentication authentication);
}
