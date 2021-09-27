package com.ironhack.midtermproject1.controller.impl;

import com.ironhack.midtermproject1.controller.interfaces.IStudentCheckingController;
import com.ironhack.midtermproject1.dao.Account;
import com.ironhack.midtermproject1.dao.Checking;
import com.ironhack.midtermproject1.dao.Money;
import com.ironhack.midtermproject1.dao.StudentChecking;
import com.ironhack.midtermproject1.enums.ReturnType;
import com.ironhack.midtermproject1.repository.StudentCheckingRepository;
import com.ironhack.midtermproject1.repository.UserRepository;
import com.ironhack.midtermproject1.service.impl.StudentCheckingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/studentCheckingAccounts")
public class StudentCheckingController implements IStudentCheckingController {

    @Autowired
    private StudentCheckingRepository studentCheckingRepository;

    @Autowired
    private StudentCheckingService studentCheckingService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<StudentChecking> getStudentCheckingAccounts(
            @RequestParam Optional<Integer> page, // allows to divide results into pages
            @RequestParam Optional<String> sortBy // allows to sort the results by any param
    ){
        return studentCheckingRepository.findAll(
                PageRequest.of(
                        page.orElse(0),
                        5,
                        Sort.Direction.ASC, sortBy.orElse("id")
                )
        );
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public StudentChecking getStudentCheckingAccountById(@PathVariable Long id){
        Optional<StudentChecking> optionalStudentChecking = studentCheckingRepository.findById(id);
        return optionalStudentChecking.orElse(null);
    }

    //allows to get accounts (all or by id) only owned by current logged user
    @GetMapping("/loggedUserAccounts")
    @ResponseStatus(HttpStatus.OK)
    public List<StudentChecking> getStudentCheckingAccountByIdAndLoggedUser(
            @Nullable @RequestParam(required = false) Long id,
            Authentication authentication){
        return studentCheckingRepository.findByIdAndPrimaryOwner(id, userRepository.findByUsername(authentication.getName()).get().getId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Account createStudentCheckingAccount(@RequestBody @Valid StudentChecking studentChecking){
        return studentCheckingService.createStudentCheckingAccount(studentChecking);
    }

    @PatchMapping("/{id}/makePayment")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Account updateBalanceAfterPayment(
            @PathVariable Long id,
            @RequestBody @Valid Money money,
            Authentication authentication) {
        return studentCheckingService.makePayment(id, money, ReturnType.STUDENT_CHECKING, userRepository.findByUsername(authentication.getName()).get().getId());
    }

    @PatchMapping("/{id}/deposit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Account updateBalanceAfterDeposit(
            @PathVariable Long id,
            @RequestBody @Valid Money money){
        return studentCheckingService.depositMoney(id, money, ReturnType.STUDENT_CHECKING);
    }

    @PatchMapping("/{sourceAccountId}/transfer")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBalanceAfterTransfer(
            @PathVariable Long sourceAccountId,
            @RequestParam Long targetAccountId,
            @RequestParam String targetAccountType,
            @Nullable @RequestParam(required = false) Optional<Long> primaryOwner,
            @Nullable @RequestParam(required = false) Optional<Long> secondaryOwner,
            @RequestBody @Valid Money money){
        studentCheckingService.transferMoney(sourceAccountId, targetAccountId, money, ReturnType.STUDENT_CHECKING, ReturnType.valueOf(targetAccountType.toUpperCase()), primaryOwner,secondaryOwner);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteStudentCheckingAccount(@PathVariable Long id){
        studentCheckingRepository.deleteById(id);
    }
}
