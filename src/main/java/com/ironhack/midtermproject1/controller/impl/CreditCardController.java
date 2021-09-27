package com.ironhack.midtermproject1.controller.impl;

import com.ironhack.midtermproject1.controller.interfaces.ICreditCardController;
import com.ironhack.midtermproject1.dao.Account;
import com.ironhack.midtermproject1.dao.Checking;
import com.ironhack.midtermproject1.dao.CreditCard;
import com.ironhack.midtermproject1.dao.Money;
import com.ironhack.midtermproject1.enums.ReturnType;
import com.ironhack.midtermproject1.repository.CreditCardRepository;
import com.ironhack.midtermproject1.repository.UserRepository;
import com.ironhack.midtermproject1.service.impl.CreditCardService;
import com.sun.istack.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/creditCards")
public class CreditCardController implements ICreditCardController {

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private CreditCardService creditCardService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<CreditCard> getCreditCards(
            @RequestParam Optional<Integer> page, // allows to divide results into pages
            @RequestParam Optional<String> sortBy // allows to sort the results by any param
    ) {
        return creditCardRepository.findAll(
                PageRequest.of(
                        page.orElse(0),
                        5,
                        Sort.Direction.ASC, sortBy.orElse("id")
                )
        );
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CreditCard getCreditCardById(@PathVariable Long id) {
        Optional<CreditCard> optionalCreditCard = creditCardRepository.findById(id);
        return optionalCreditCard.orElse(null);
    }

    //allows to get accounts (all or by id) only owned by current logged user
    @GetMapping("/loggedUserAccounts")
    @ResponseStatus(HttpStatus.OK)
    public List<CreditCard> getCreditCardByIdAndLoggedUser(
            @Nullable @RequestParam(required = false) Long id,
            Authentication authentication){
        return creditCardRepository.findByIdAndPrimaryOwner(id, userRepository.findByUsername(authentication.getName()).get().getId());
    }

    //allows to write query with any WHERE statements
    @GetMapping("/getByAnyParams")
    @ResponseStatus(HttpStatus.OK)
    public List<CreditCard> getCreditCardByAnyParameter(
            @Nullable @RequestParam(required = false) BigDecimal maxBalance,
            @Nullable @RequestParam(required = false) BigDecimal minBalance,
            @Nullable @RequestParam(required = false) BigDecimal maxCreditLimit,
            @Nullable @RequestParam(required = false) BigDecimal minCreditLimit,
            @RequestParam(defaultValue = "9999-01-01") String maxCreationDate,
            @RequestParam(defaultValue = "1900-01-01") String minCreationDate,
            @RequestParam(defaultValue = "9999-01-01") String maxAdditionLastInterestDate,
            @RequestParam(defaultValue = "1900-01-01") String minAdditionLastInterestDate,
            @Nullable @RequestParam(required = false) BigDecimal maxInterestRate,
            @Nullable @RequestParam(required = false) BigDecimal minInterestRate,
            @Nullable @RequestParam(required = false) Long primaryOwner,
            @Nullable @RequestParam(required = false) Long secondaryOwner
    ) {
        return creditCardRepository.findByAnyParams(
                maxBalance,
                minBalance,
                maxCreditLimit,
                minCreditLimit,
                maxCreationDate,
                minCreationDate,
                maxAdditionLastInterestDate,
                minAdditionLastInterestDate,
                maxInterestRate,
                minInterestRate,
                primaryOwner,
                secondaryOwner);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreditCard createCreditCard(@RequestBody @Valid CreditCard creditCard){
        return creditCardService.createCreditCard(creditCard);
    }

    @PatchMapping("/{id}/addInterest")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CreditCard updateCreditCardBalanceByAddingInterests(@PathVariable Long id) {
        return creditCardService.updateCreditCardBalanceByAddingInterests(id);
    }

    @PatchMapping("/{id}/makePayment")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Account updateBalanceAfterPayment(
            @PathVariable Long id,
            @RequestBody @Valid Money money,
            Authentication authentication) {
        return creditCardService.makePayment(id, money, ReturnType.CREDIT_CARD, userRepository.findByUsername(authentication.getName()).get().getId());
    }

    @PatchMapping("/{id}/deposit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Account updateBalanceAfterDeposit(
            @PathVariable Long id,
            @RequestBody @Valid Money money) {
        return creditCardService.depositMoney(id, money, ReturnType.CREDIT_CARD);
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
        creditCardService.transferMoney(sourceAccountId, targetAccountId, money, ReturnType.CREDIT_CARD, ReturnType.valueOf(targetAccountType.toUpperCase()), primaryOwner,secondaryOwner);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCreditCard(@PathVariable Long id){
        creditCardRepository.deleteById(id);
    }
}