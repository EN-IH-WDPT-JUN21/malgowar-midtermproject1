package com.ironhack.midtermproject1.service.impl;

import com.ironhack.midtermproject1.dao.Account;
import com.ironhack.midtermproject1.dao.AccountHolder;
import com.ironhack.midtermproject1.dao.Checking;
import com.ironhack.midtermproject1.dao.StudentChecking;
import com.ironhack.midtermproject1.repository.AccountHolderRepository;
import com.ironhack.midtermproject1.repository.CheckingRepository;
import com.ironhack.midtermproject1.repository.StudentCheckingRepository;
import com.ironhack.midtermproject1.service.interfaces.IStudentCheckingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class StudentCheckingService extends AccountService implements IStudentCheckingService {

    @Autowired
    private CheckingRepository checkingRepository;

    @Autowired
    private StudentCheckingRepository studentCheckingRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    //post
    public Account createStudentCheckingAccount(StudentChecking studentChecking) {
        LocalDate today = LocalDate.now();
        if (studentChecking.getPrimaryOwner() != null) {
            Optional<AccountHolder> primaryOwner = accountHolderRepository.findById(studentChecking.getPrimaryOwner().getId());

            if(primaryOwner.isPresent()) {
                long diff = Math.abs(ChronoUnit.YEARS.between(today, primaryOwner.get().getBirthDate()));
                AccountHolder secondaryOwner;

                if(studentChecking.getSecondaryOwner() != null) {
                    if(accountHolderRepository.findById(studentChecking.getSecondaryOwner().getId()).isPresent()){
                        secondaryOwner = accountHolderRepository.findById(studentChecking.getSecondaryOwner().getId()).get();
                    } else {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The Secondary Owner with the given id does not exist");
                    }
                } else {
                    secondaryOwner = null;
                }
                if (diff < 24) {
                    studentChecking.setPrimaryOwner(primaryOwner.get());
                    studentChecking.setSecondaryOwner(secondaryOwner);
                    return studentCheckingRepository.save(studentChecking);
                } else {
                    Checking checkingAccount = new Checking(studentChecking.getBalance(), studentChecking.getCurrency(), primaryOwner.get(), secondaryOwner,
                            studentChecking.getCreationDate(), studentChecking.getSecretKey(), studentChecking.getStatus());
                    return checkingRepository.save(checkingAccount);
                }
            } else{
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The Primary Owner with the given id does not exist");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student checking account must have Primary Owner");
        }
    }
}
