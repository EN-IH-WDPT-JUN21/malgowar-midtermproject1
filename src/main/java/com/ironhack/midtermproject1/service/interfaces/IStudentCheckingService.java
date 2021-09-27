package com.ironhack.midtermproject1.service.interfaces;

import com.ironhack.midtermproject1.dao.Account;
import com.ironhack.midtermproject1.dao.StudentChecking;

public interface IStudentCheckingService {
    Account createStudentCheckingAccount(StudentChecking studentChecking);
}
