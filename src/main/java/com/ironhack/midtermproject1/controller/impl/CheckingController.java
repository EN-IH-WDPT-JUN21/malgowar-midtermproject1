package com.ironhack.midtermproject1.controller.impl;

import com.ironhack.midtermproject1.controller.interfaces.ICheckingController;
import com.ironhack.midtermproject1.dao.Account;
import com.ironhack.midtermproject1.dao.Checking;
import com.ironhack.midtermproject1.dao.Money;
import com.ironhack.midtermproject1.enums.ReturnType;
import com.ironhack.midtermproject1.repository.CheckingRepository;
import com.ironhack.midtermproject1.repository.UserRepository;
import com.ironhack.midtermproject1.service.impl.CheckingService;
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
@RequestMapping("/api/v1/checkingAccounts")
public class CheckingController implements ICheckingController {

    @Autowired
    private CheckingRepository checkingRepository;

    @Autowired
    private CheckingService checkingService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public Page<Checking> getCheckingAccounts(
            @RequestParam Optional<Integer> page, // allows to divide results into pages
            @RequestParam Optional<String> sortBy // allows to sort the results by any param
    ){
        return checkingRepository.findAll(
                PageRequest.of(
                        page.orElse(0),
                        5,
                        Sort.Direction.ASC, sortBy.orElse("id")
                )
        );
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Checking getCheckingAccountById(
            @PathVariable Long id){
        Optional<Checking> optionalChecking = checkingRepository.findById(id);
        return optionalChecking.orElse(null);
    }

    //allows to get accounts (all or by id) only owned by current logged user
    @GetMapping("/loggedUserAccounts")
    @ResponseStatus(HttpStatus.OK)
    public List<Checking> getCheckingAccountByIdAndLoggedUser(
            @Nullable @RequestParam(required = false) Long id,
            Authentication authentication){
        return checkingRepository.findByIdAndPrimaryOwner(id, userRepository.findByUsername(authentication.getName()).get().getId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Account createCheckingAccount(@RequestBody @Valid Checking checking){
        return checkingService.createCheckingAccount(checking);
    }

    @PatchMapping("/{id}/makePayment")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Account updateBalanceAfterPayment(
            @PathVariable Long id,
            @RequestBody @Valid Money money,
            Authentication authentication){
        return checkingService.makePayment(id, money, ReturnType.CHECKING, userRepository.findByUsername(authentication.getName()).get().getId());
    }

    @PatchMapping("/{id}/deposit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Account updateBalanceAfterDeposit(
            @PathVariable Long id,
            @RequestBody @Valid Money money){
        return checkingService.depositMoney(id, money, ReturnType.CHECKING);
    }

    // allows to charge fee for selected checking account or for all (id = null)
    @PatchMapping("/chargeFee")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void chargeMaintanenceFeeForAllOrAnyAccounts(
            @Nullable @RequestParam(required = false) Long id){
        checkingService.chargeMaintanenceFee(id);
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
        checkingService.transferMoney(sourceAccountId, targetAccountId, money, ReturnType.CHECKING, ReturnType.valueOf(targetAccountType.toUpperCase()), primaryOwner,secondaryOwner);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCheckingAccount(@PathVariable Long id){
        checkingRepository.deleteById(id);
    }
}
