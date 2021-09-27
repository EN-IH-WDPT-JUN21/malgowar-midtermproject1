package com.ironhack.midtermproject1.controller.impl;

import com.ironhack.midtermproject1.controller.interfaces.ISavingController;
import com.ironhack.midtermproject1.dao.Account;
import com.ironhack.midtermproject1.dao.Money;
import com.ironhack.midtermproject1.dao.Saving;
import com.ironhack.midtermproject1.enums.ReturnType;
import com.ironhack.midtermproject1.repository.SavingRepository;
import com.ironhack.midtermproject1.repository.UserRepository;
import com.ironhack.midtermproject1.service.impl.SavingService;
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
@RequestMapping("/api/v1/savingAccounts")
public class SavingController implements ISavingController {

    @Autowired
    private SavingRepository savingRepository;

    @Autowired
    private SavingService savingService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<Saving> getSavingAccounts(
            @RequestParam Optional<Integer> page, // allows to divide results into pages
            @RequestParam Optional<String> sortBy // allows to sort the results by any param
    ){
        return savingRepository.findAll(
                PageRequest.of(
                        page.orElse(0),
                        5,
                        Sort.Direction.ASC, sortBy.orElse("id")
                )
        );
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Saving getSavingById(@PathVariable Long id){
        Optional<Saving> optionalSaving = savingRepository.findById(id);
        return optionalSaving.orElse(null);
    }

    //allows to get accounts (all or by id) only owned by current logged user
    @GetMapping("/loggedUserAccounts")
    @ResponseStatus(HttpStatus.OK)
    public List<Saving> getSavingByIdAndLoggedUser(
            @Nullable @RequestParam(required = false) Long id,
            Authentication authentication){
        return savingRepository.findByIdAndPrimaryOwner(id, userRepository.findByUsername(authentication.getName()).get().getId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Saving createSaving(@RequestBody @Valid Saving savingAccount){
        return savingService.createSavingAccount(savingAccount);
    }

    @PatchMapping("/{id}/addInterest")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Saving updateSavingBalanceByAddingInterests(@PathVariable Long id){
        return savingService.updateSavingBalanceByAddingInterests(id);
    }

    @PatchMapping("/{id}/makePayment")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Account updateBalanceAfterPayment(
            @PathVariable Long id,
            @RequestBody @Valid Money money,
            Authentication authentication) {
        return savingService.makePayment(id, money, ReturnType.SAVING, userRepository.findByUsername(authentication.getName()).get().getId());
    }

    @PatchMapping("/{id}/deposit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Account updateBalanceAfterDeposit(
            @PathVariable Long id,
            @RequestBody @Valid Money money) {
        return savingService.depositMoney(id, money, ReturnType.SAVING);
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
        savingService.transferMoney(sourceAccountId, targetAccountId, money, ReturnType.SAVING, ReturnType.valueOf(targetAccountType.toUpperCase()), primaryOwner,secondaryOwner);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteSavingAccount(@PathVariable Long id){
        savingRepository.deleteById(id);
    }
}
