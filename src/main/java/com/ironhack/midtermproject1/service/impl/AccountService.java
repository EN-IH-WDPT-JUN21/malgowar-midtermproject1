package com.ironhack.midtermproject1.service.impl;

import com.ironhack.midtermproject1.dao.*;
import com.ironhack.midtermproject1.enums.AccountStatus;
import com.ironhack.midtermproject1.enums.ReturnType;
import com.ironhack.midtermproject1.enums.TransactionType;
import com.ironhack.midtermproject1.repository.*;
import com.ironhack.midtermproject1.service.interfaces.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService implements IAccountService {

    @Autowired
    private CheckingRepository checkingRepository;

    @Autowired
    private StudentCheckingRepository studentCheckingRepository;

    @Autowired
    private SavingRepository savingRepository;

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    public List<BigDecimal> subtractFunds(BigDecimal balance, BigDecimal amount, BigDecimal minimumBalance, BigDecimal penaltyFee, BigDecimal creditLimit) {
        List<BigDecimal> balanceAmountList = new ArrayList<>();
        if (balance.add(creditLimit).compareTo(amount) >= 0) {
            if (balance.add(creditLimit).subtract(amount).compareTo(minimumBalance) >= 0) {
                balance = balance.subtract(amount);
            } else {
                balance = balance.subtract(amount).subtract(penaltyFee);
                amount = amount.add(penaltyFee);
            }
            balanceAmountList.add(balance);
            balanceAmountList.add(amount);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There are no such funds on the account");
        }
        return balanceAmountList;
    }

    public List<BigDecimal> addFunds(BigDecimal balance, BigDecimal amount) {
        List<BigDecimal> balanceAmountList = new ArrayList<>();
        balance = balance.add(amount);
        balanceAmountList.add(balance);
        balanceAmountList.add(amount);
        return balanceAmountList;
    }

    // generic make Payment method for any type of account
    public Account makePayment(Long id, Money money, ReturnType accountType, Long primaryOwnerId) {
        if (money.getBalance().compareTo(new BigDecimal(0)) > 0) {
            List<BigDecimal> balanceAmountList;
            if (accountType == ReturnType.CHECKING) {

                Optional<Checking> optionalChecking = checkingRepository.findById(id);
                if (optionalChecking.isPresent()) {
                    if(optionalChecking.get().getStatus() != AccountStatus.FROZEN) {
                        if(optionalChecking.get().getPrimaryOwner().getId() == primaryOwnerId) {
                            balanceAmountList = subtractFunds(optionalChecking.get().getBalance(), money.getBalance(), optionalChecking.get().getMinimumBalance(), optionalChecking.get().getPenaltyFee(), new BigDecimal(0));
                            optionalChecking.get().setBalance(balanceAmountList.get(0));
                            transactionService.saveTransaction(TransactionType.PAYMENT, balanceAmountList.get(1), optionalChecking.get().getId(), accountType, optionalChecking.get().getPrimaryOwner(), optionalChecking.get().getId(), accountType, optionalChecking.get().getPrimaryOwner());
                            return checkingRepository.save(optionalChecking.get());
                        } else {
                            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You have no access to the account with the given id");
                        }
                    } else {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The checking account with the given id is frozen");
                    }
                } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The checking account with the given id does not exist");
                }
            } else if (accountType == ReturnType.STUDENT_CHECKING) {

                Optional<StudentChecking> optionalStudentChecking = studentCheckingRepository.findById(id);
                if (optionalStudentChecking.isPresent()) {
                    if(optionalStudentChecking.get().getStatus() != AccountStatus.FROZEN) {
                        if(optionalStudentChecking.get().getPrimaryOwner().getId() == primaryOwnerId) {
                            balanceAmountList = subtractFunds(optionalStudentChecking.get().getBalance(), money.getBalance(), new BigDecimal(0), optionalStudentChecking.get().getPenaltyFee(), new BigDecimal(0));
                            optionalStudentChecking.get().setBalance(balanceAmountList.get(0));
                            transactionService.saveTransaction(TransactionType.PAYMENT, balanceAmountList.get(1), optionalStudentChecking.get().getId(), accountType, optionalStudentChecking.get().getPrimaryOwner(), optionalStudentChecking.get().getId(), accountType, optionalStudentChecking.get().getPrimaryOwner());
                            return studentCheckingRepository.save(optionalStudentChecking.get());
                        } else {
                            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You have no access to the account with the given id");
                        }
                    } else {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The student checking account with the given id is frozen");
                    }
                } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The student checking account with the given id does not exist");
                }
            } else if (accountType == ReturnType.SAVING) {

                Optional<Saving> optionalSaving = savingRepository.findById(id);
                if (optionalSaving.isPresent()) {
                    if(optionalSaving.get().getStatus() != AccountStatus.FROZEN) {
                        if(optionalSaving.get().getPrimaryOwner().getId() == primaryOwnerId) {
                            balanceAmountList = subtractFunds(optionalSaving.get().getBalance(), money.getBalance(), optionalSaving.get().getMinimumBalance(), optionalSaving.get().getPenaltyFee(), new BigDecimal(0));
                            optionalSaving.get().setBalance(balanceAmountList.get(0));
                            transactionService.saveTransaction(TransactionType.PAYMENT, balanceAmountList.get(1), optionalSaving.get().getId(), accountType, optionalSaving.get().getPrimaryOwner(), optionalSaving.get().getId(), accountType, optionalSaving.get().getPrimaryOwner());
                            return savingRepository.save(optionalSaving.get());
                        } else {
                            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You have no access to the account with the given id");
                        }
                    } else {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The saving account with the given id is frozen");
                    }
                } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The saving account with the given id does not exist");
                }
            } else if (accountType == ReturnType.CREDIT_CARD) {

                Optional<CreditCard> optionalCreditCard = creditCardRepository.findById(id);
                if (optionalCreditCard.isPresent()) {
                    if(optionalCreditCard.get().getPrimaryOwner().getId() == primaryOwnerId) {
                        balanceAmountList = subtractFunds(optionalCreditCard.get().getBalance(), money.getBalance(), new BigDecimal(0), optionalCreditCard.get().getPenaltyFee(), optionalCreditCard.get().getCreditLimit());
                        optionalCreditCard.get().setBalance(balanceAmountList.get(0));
                        transactionService.saveTransaction(TransactionType.PAYMENT, balanceAmountList.get(1), optionalCreditCard.get().getId(), accountType, optionalCreditCard.get().getPrimaryOwner(), optionalCreditCard.get().getId(), accountType, optionalCreditCard.get().getPrimaryOwner());
                        return creditCardRepository.save(optionalCreditCard.get());
                    }else {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You have no access to the account with the given id");
                    }
                } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The credit card with the given id does not exist");
                }
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no such account type");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The amount cannot be less then 0");
        }
    }

    // generic deposit money method for any type of account
    public Account depositMoney(Long id, Money money, ReturnType accountType) {
        if (money.getBalance().compareTo(new BigDecimal(0)) > 0) {
            List<BigDecimal> balanceAmountList;
            if (accountType == ReturnType.CHECKING) {

                Optional<Checking> optionalChecking = checkingRepository.findById(id);
                if (optionalChecking.isPresent()) {
                    if(optionalChecking.get().getStatus() != AccountStatus.FROZEN){
                        balanceAmountList = addFunds(optionalChecking.get().getBalance(), money.getBalance());
                        optionalChecking.get().setBalance(balanceAmountList.get(0));
                        transactionService.saveTransaction(TransactionType.DEPOSIT, balanceAmountList.get(1), optionalChecking.get().getId(), accountType, optionalChecking.get().getPrimaryOwner(), optionalChecking.get().getId(), accountType, optionalChecking.get().getPrimaryOwner());
                        return checkingRepository.save(optionalChecking.get());
                    } else {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The checking account with the given id is frozen");
                    }
                } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The checking account with the given id does not exist");
                }
            } else if (accountType == ReturnType.STUDENT_CHECKING) {

                Optional<StudentChecking> optionalStudentChecking = studentCheckingRepository.findById(id);
                if (optionalStudentChecking.isPresent()) {
                    if(optionalStudentChecking.get().getStatus() != AccountStatus.FROZEN) {
                        balanceAmountList = addFunds(optionalStudentChecking.get().getBalance(), money.getBalance());
                        optionalStudentChecking.get().setBalance(balanceAmountList.get(0));
                        transactionService.saveTransaction(TransactionType.DEPOSIT, balanceAmountList.get(1), optionalStudentChecking.get().getId(), accountType, optionalStudentChecking.get().getPrimaryOwner(), optionalStudentChecking.get().getId(), accountType, optionalStudentChecking.get().getPrimaryOwner());
                        return studentCheckingRepository.save(optionalStudentChecking.get());
                    } else {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The student checking account with the given id is frozen");
                    }
                } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The student checking account with the given id does not exist");
                }
            } else if (accountType == ReturnType.SAVING) {

                Optional<Saving> optionalSaving = savingRepository.findById(id);
                if (optionalSaving.isPresent()) {
                    if(optionalSaving.get().getStatus() != AccountStatus.FROZEN) {
                        balanceAmountList = addFunds(optionalSaving.get().getBalance(), money.getBalance());
                        optionalSaving.get().setBalance(balanceAmountList.get(0));
                        transactionService.saveTransaction(TransactionType.DEPOSIT, balanceAmountList.get(1), optionalSaving.get().getId(), accountType, optionalSaving.get().getPrimaryOwner(), optionalSaving.get().getId(), accountType, optionalSaving.get().getPrimaryOwner());
                        return savingRepository.save(optionalSaving.get());
                    } else {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The saving account with the given id is frozen");
                    }
                } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The saving account with the given id does not exist");
                }
            } else if (accountType == ReturnType.CREDIT_CARD) {

                Optional<CreditCard> optionalCreditCard = creditCardRepository.findById(id);
                if (optionalCreditCard.isPresent()) {
                    balanceAmountList = addFunds(optionalCreditCard.get().getBalance(), money.getBalance());
                    optionalCreditCard.get().setBalance(balanceAmountList.get(0));
                    transactionService.saveTransaction(TransactionType.DEPOSIT, balanceAmountList.get(1), optionalCreditCard.get().getId(), accountType, optionalCreditCard.get().getPrimaryOwner(), optionalCreditCard.get().getId(), accountType, optionalCreditCard.get().getPrimaryOwner());
                    return creditCardRepository.save(optionalCreditCard.get());
                } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The credit card with the given id does not exist");
                }
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no such account type");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The amount cannot be less then 0");
        }
    }

    public void transferMoney(Long sourceAccountId, Long targetAccountId, Money money, ReturnType sourceAccountType, ReturnType targetAccountType, Optional<Long> primaryOwner, Optional<Long> secondaryOwner) {
        if (money.getBalance().compareTo(new BigDecimal(0)) > 0) {
            List<BigDecimal> subBalanceAmountList;
            if (sourceAccountType == ReturnType.CHECKING) {
                Optional<Checking> sourceAccount = checkingRepository.findById(sourceAccountId);

                if (sourceAccount.isPresent() ) {
                    if(sourceAccount.get().getStatus() != AccountStatus.FROZEN) {
                        subBalanceAmountList = subtractFunds(sourceAccount.get().getBalance(), money.getBalance(), sourceAccount.get().getMinimumBalance(), sourceAccount.get().getPenaltyFee(), new BigDecimal(0));

                        if (targetAccountType == ReturnType.CHECKING) {
                            Optional<Checking> targetAccount = checkingRepository.findById(targetAccountId);
                            if (targetAccount.isPresent()){
                                if(targetAccount.get().getStatus() != AccountStatus.FROZEN) {
                                    targetAccount.get().setBalance(addFunds(targetAccount.get().getBalance(), money.getBalance()).get(0));
                                    checkingRepository.save(targetAccount.get());
                                    transactionService.saveTransaction(TransactionType.TRANSFER, money.getBalance(), sourceAccount.get().getId(), sourceAccountType, sourceAccount.get().getPrimaryOwner(), targetAccount.get().getId(), targetAccountType, targetAccount.get().getPrimaryOwner());
                                } else {
                                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The checking account with the given id is frozen");
                                }
                            } else {
                                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The checking account with the given id does not exist");
                            }
                        } else if (targetAccountType == ReturnType.STUDENT_CHECKING) {
                            Optional<StudentChecking> targetAccount = studentCheckingRepository.findById(targetAccountId);
                            if (targetAccount.isPresent() ){
                                if(targetAccount.get().getStatus() != AccountStatus.FROZEN){
                                    targetAccount.get().setBalance(addFunds(targetAccount.get().getBalance(), money.getBalance()).get(0));
                                    studentCheckingRepository.save(targetAccount.get());
                                    transactionService.saveTransaction(TransactionType.TRANSFER, money.getBalance(), sourceAccount.get().getId(), sourceAccountType, sourceAccount.get().getPrimaryOwner(), targetAccount.get().getId(), targetAccountType, targetAccount.get().getPrimaryOwner());
                                } else {
                                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The student checking account with the given id is frozen");
                                }
                            } else {
                                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The student checking account with the given id does not exist");
                            }
                        } else if (targetAccountType == ReturnType.SAVING) {
                            Optional<Saving> targetAccount = savingRepository.findById(targetAccountId);
                            if (targetAccount.isPresent()){
                                if(targetAccount.get().getStatus() != AccountStatus.FROZEN) {
                                    targetAccount.get().setBalance(addFunds(targetAccount.get().getBalance(), money.getBalance()).get(0));
                                    savingRepository.save(targetAccount.get());
                                    transactionService.saveTransaction(TransactionType.TRANSFER, money.getBalance(), sourceAccount.get().getId(), sourceAccountType, sourceAccount.get().getPrimaryOwner(), targetAccount.get().getId(), targetAccountType, targetAccount.get().getPrimaryOwner());
                                } else {
                                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The saving account with the given id is frozen");
                                }
                            } else {
                                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The saving account with the given id does not exist");
                            }
                        } else if (targetAccountType == ReturnType.CREDIT_CARD) {
                            Optional<CreditCard> targetAccount = creditCardRepository.findById(targetAccountId);
                            if (targetAccount.isPresent()) {
                                targetAccount.get().setBalance(addFunds(targetAccount.get().getBalance(), money.getBalance()).get(0));
                                creditCardRepository.save(targetAccount.get());
                                transactionService.saveTransaction(TransactionType.TRANSFER, money.getBalance(), sourceAccount.get().getId(), sourceAccountType, sourceAccount.get().getPrimaryOwner(), targetAccount.get().getId(), targetAccountType, targetAccount.get().getPrimaryOwner());
                            } else {
                                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The saving account with the given id does not exist");
                            }
                        }
                        sourceAccount.get().setBalance(subBalanceAmountList.get(0));
                        checkingRepository.save(sourceAccount.get());
                    }else{
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The checking account with the given id is frozen");
                    }
                } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The checking account with the given id does not exist");
                }
            } else if (sourceAccountType == ReturnType.STUDENT_CHECKING) {
                Optional<StudentChecking> sourceAccount = studentCheckingRepository.findById(sourceAccountId);

                if (sourceAccount.isPresent()){
                    if(sourceAccount.get().getStatus() != AccountStatus.FROZEN) {
                        subBalanceAmountList = subtractFunds(sourceAccount.get().getBalance(), money.getBalance(), new BigDecimal(0), sourceAccount.get().getPenaltyFee(), new BigDecimal(0));

                        if (targetAccountType == ReturnType.CHECKING) {
                            Optional<Checking> targetAccount = checkingRepository.findById(targetAccountId);
                            if (targetAccount.isPresent()){
                                if(targetAccount.get().getStatus() != AccountStatus.FROZEN) {
                                    targetAccount.get().setBalance(addFunds(targetAccount.get().getBalance(), money.getBalance()).get(0));
                                    checkingRepository.save(targetAccount.get());
                                    transactionService.saveTransaction(TransactionType.TRANSFER, money.getBalance(), sourceAccount.get().getId(), sourceAccountType, sourceAccount.get().getPrimaryOwner(), targetAccount.get().getId(), targetAccountType, targetAccount.get().getPrimaryOwner());
                                } else {
                                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The checking account with the given id is frozen");
                                }
                            } else {
                                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The checking account with the given id does not exist");
                            }
                        } else if (targetAccountType == ReturnType.STUDENT_CHECKING) {
                            Optional<StudentChecking> targetAccount = studentCheckingRepository.findById(targetAccountId);
                            if (targetAccount.isPresent()){
                                if(targetAccount.get().getStatus() != AccountStatus.FROZEN) {
                                    targetAccount.get().setBalance(addFunds(targetAccount.get().getBalance(), money.getBalance()).get(0));
                                    studentCheckingRepository.save(targetAccount.get());
                                    transactionService.saveTransaction(TransactionType.TRANSFER, money.getBalance(), sourceAccount.get().getId(), sourceAccountType, sourceAccount.get().getPrimaryOwner(), targetAccount.get().getId(), targetAccountType, targetAccount.get().getPrimaryOwner());
                                } else{
                                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "he student checking account with the given id is frozen");
                                }
                            } else {
                                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The student checking account with the given id does not exist");
                            }
                        } else if (targetAccountType == ReturnType.SAVING) {
                            Optional<Saving> targetAccount = savingRepository.findById(targetAccountId);
                            if (targetAccount.isPresent()){
                                if(targetAccount.get().getStatus() != AccountStatus.FROZEN) {
                                    targetAccount.get().setBalance(addFunds(targetAccount.get().getBalance(), money.getBalance()).get(0));
                                    savingRepository.save(targetAccount.get());
                                    transactionService.saveTransaction(TransactionType.TRANSFER, money.getBalance(), sourceAccount.get().getId(), sourceAccountType, sourceAccount.get().getPrimaryOwner(), targetAccount.get().getId(), targetAccountType, targetAccount.get().getPrimaryOwner());
                                } else {
                                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "he saving account with the given id is frozen");
                                }
                            } else {
                                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The saving account with the given id does not exist");
                            }
                        } else if (targetAccountType == ReturnType.CREDIT_CARD) {
                            Optional<CreditCard> targetAccount = creditCardRepository.findById(targetAccountId);
                            if (targetAccount.isPresent()) {
                                targetAccount.get().setBalance(addFunds(targetAccount.get().getBalance(), money.getBalance()).get(0));
                                creditCardRepository.save(targetAccount.get());
                                transactionService.saveTransaction(TransactionType.TRANSFER, money.getBalance(), sourceAccount.get().getId(), sourceAccountType, sourceAccount.get().getPrimaryOwner(), targetAccount.get().getId(), targetAccountType, targetAccount.get().getPrimaryOwner());
                            } else {
                                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The saving account with the given id does not exist");
                            }
                        }
                        sourceAccount.get().setBalance(subBalanceAmountList.get(0));
                        studentCheckingRepository.save(sourceAccount.get());
                    }else {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The student checking account with the given id is frozen");
                    }
                } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The student checking account with the given id does not exist");
                }

            } else if (sourceAccountType == ReturnType.SAVING) {
                Optional<Saving> sourceAccount = savingRepository.findById(sourceAccountId);

                if (sourceAccount.isPresent()){
                    if(sourceAccount.get().getStatus() != AccountStatus.FROZEN) {
                        subBalanceAmountList = subtractFunds(sourceAccount.get().getBalance(), money.getBalance(), sourceAccount.get().getMinimumBalance(), sourceAccount.get().getPenaltyFee(), new BigDecimal(0));

                        if (targetAccountType == ReturnType.CHECKING) {
                            Optional<Checking> targetAccount = checkingRepository.findById(targetAccountId);
                            if (targetAccount.isPresent()){
                                if(targetAccount.get().getStatus() != AccountStatus.FROZEN) {
                                    targetAccount.get().setBalance(addFunds(targetAccount.get().getBalance(), money.getBalance()).get(0));
                                    checkingRepository.save(targetAccount.get());
                                    transactionService.saveTransaction(TransactionType.TRANSFER, money.getBalance(), sourceAccount.get().getId(), sourceAccountType, sourceAccount.get().getPrimaryOwner(), targetAccount.get().getId(), targetAccountType, targetAccount.get().getPrimaryOwner());
                                }else {
                                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The checking checking account with the given id is frozen");
                                }
                            } else {
                                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The checking account with the given id does not exist");
                            }
                        } else if (targetAccountType == ReturnType.STUDENT_CHECKING) {
                            Optional<StudentChecking> targetAccount = studentCheckingRepository.findById(targetAccountId);
                            if (targetAccount.isPresent()){
                                if(targetAccount.get().getStatus() != AccountStatus.FROZEN) {
                                    targetAccount.get().setBalance(addFunds(targetAccount.get().getBalance(), money.getBalance()).get(0));
                                    studentCheckingRepository.save(targetAccount.get());
                                    transactionService.saveTransaction(TransactionType.TRANSFER, money.getBalance(), sourceAccount.get().getId(), sourceAccountType, sourceAccount.get().getPrimaryOwner(), targetAccount.get().getId(), targetAccountType, targetAccount.get().getPrimaryOwner());
                                } else {
                                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The student checking account with the given id is frozen");
                                }
                            } else {
                                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The student checking account with the given id does not exist");
                            }
                        } else if (targetAccountType == ReturnType.SAVING) {
                            Optional<Saving> targetAccount = savingRepository.findById(targetAccountId);
                            if (targetAccount.isPresent()){
                                if(targetAccount.get().getStatus() != AccountStatus.FROZEN) {
                                    targetAccount.get().setBalance(addFunds(targetAccount.get().getBalance(), money.getBalance()).get(0));
                                    savingRepository.save(targetAccount.get());
                                    transactionService.saveTransaction(TransactionType.TRANSFER, money.getBalance(), sourceAccount.get().getId(), sourceAccountType, sourceAccount.get().getPrimaryOwner(), targetAccount.get().getId(), targetAccountType, targetAccount.get().getPrimaryOwner());
                                } else {
                                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The saving checking account with the given id is frozen");
                                }
                            } else {
                                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The saving account with the given id does not exist");
                            }
                        } else if (targetAccountType == ReturnType.CREDIT_CARD) {
                            Optional<CreditCard> targetAccount = creditCardRepository.findById(targetAccountId);
                            if (targetAccount.isPresent()) {
                                targetAccount.get().setBalance(addFunds(targetAccount.get().getBalance(), money.getBalance()).get(0));
                                creditCardRepository.save(targetAccount.get());
                                transactionService.saveTransaction(TransactionType.TRANSFER, money.getBalance(), sourceAccount.get().getId(), sourceAccountType, sourceAccount.get().getPrimaryOwner(), targetAccount.get().getId(), targetAccountType, targetAccount.get().getPrimaryOwner());
                            } else {
                                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The saving account with the given id does not exist");
                            }
                        }
                        sourceAccount.get().setBalance(subBalanceAmountList.get(0));
                        savingRepository.save(sourceAccount.get());
                    } else {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The saving account with the given id is frozen");
                    }
                } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The saving account with the given id does not exist");
                }
            } else if (sourceAccountType == ReturnType.CREDIT_CARD) {
                Optional<CreditCard> sourceAccount = creditCardRepository.findById(sourceAccountId);

                if (sourceAccount.isPresent()) {
                    subBalanceAmountList = subtractFunds(sourceAccount.get().getBalance(), money.getBalance(), new BigDecimal(0), sourceAccount.get().getPenaltyFee(), sourceAccount.get().getCreditLimit());

                    if (targetAccountType == ReturnType.CHECKING) {
                        Optional<Checking> targetAccount = checkingRepository.findById(targetAccountId);
                        if (targetAccount.isPresent()){
                            if(targetAccount.get().getStatus() != AccountStatus.FROZEN) {
                                targetAccount.get().setBalance(addFunds(targetAccount.get().getBalance(), money.getBalance()).get(0));
                                checkingRepository.save(targetAccount.get());
                                transactionService.saveTransaction(TransactionType.TRANSFER, money.getBalance(), sourceAccount.get().getId(), sourceAccountType, sourceAccount.get().getPrimaryOwner(), targetAccount.get().getId(), targetAccountType, targetAccount.get().getPrimaryOwner());
                            } else {
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The checking account with the given id is frozen");
                            }
                        } else {
                            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The checking account with the given id does not exist");
                        }
                    } else if (targetAccountType == ReturnType.STUDENT_CHECKING) {
                        Optional<StudentChecking> targetAccount = studentCheckingRepository.findById(targetAccountId);
                        if (targetAccount.isPresent()){
                            if(targetAccount.get().getStatus() != AccountStatus.FROZEN) {
                                targetAccount.get().setBalance(addFunds(targetAccount.get().getBalance(), money.getBalance()).get(0));
                                studentCheckingRepository.save(targetAccount.get());
                                transactionService.saveTransaction(TransactionType.TRANSFER, money.getBalance(), sourceAccount.get().getId(), sourceAccountType, sourceAccount.get().getPrimaryOwner(), targetAccount.get().getId(), targetAccountType, targetAccount.get().getPrimaryOwner());
                            }else{
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The student checking account with the given id is frozen");
                            }
                        } else {
                            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The student checking account with the given id does not exist");
                        }
                    } else if (targetAccountType == ReturnType.SAVING) {
                        Optional<Saving> targetAccount = savingRepository.findById(targetAccountId);
                        if (targetAccount.isPresent()){
                            if(targetAccount.get().getStatus() != AccountStatus.FROZEN) {
                                targetAccount.get().setBalance(addFunds(targetAccount.get().getBalance(), money.getBalance()).get(0));
                                savingRepository.save(targetAccount.get());
                                transactionService.saveTransaction(TransactionType.TRANSFER, money.getBalance(), sourceAccount.get().getId(), sourceAccountType, sourceAccount.get().getPrimaryOwner(), targetAccount.get().getId(), targetAccountType, targetAccount.get().getPrimaryOwner());
                            }else {
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The saving account with the given id is frozen");
                            }
                        } else {
                            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The saving account with the given id does not exist");
                        }
                    } else if (targetAccountType == ReturnType.CREDIT_CARD) {
                        Optional<CreditCard> targetAccount = creditCardRepository.findById(targetAccountId);
                        if (targetAccount.isPresent()) {
                            targetAccount.get().setBalance(addFunds(targetAccount.get().getBalance(), money.getBalance()).get(0));
                            creditCardRepository.save(targetAccount.get());
                            transactionService.saveTransaction(TransactionType.TRANSFER, money.getBalance(), sourceAccount.get().getId(), sourceAccountType, sourceAccount.get().getPrimaryOwner(), targetAccount.get().getId(), targetAccountType, targetAccount.get().getPrimaryOwner());
                        } else {
                            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The saving account with the given id does not exist");
                        }
                    }
                    sourceAccount.get().setBalance(subBalanceAmountList.get(0));
                    creditCardRepository.save(sourceAccount.get());
                } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The credit card with the given id does not exist");
                }
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no such account type");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The amount cannot be less then 0");
        }
    }
}
