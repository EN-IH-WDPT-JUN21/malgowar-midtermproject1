package com.ironhack.midtermproject1.dao;

import com.ironhack.midtermproject1.enums.ReturnType;
import com.ironhack.midtermproject1.enums.TransactionType;
import com.ironhack.midtermproject1.utils.dataValidator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Transaction date cannot be null")
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate = dataValidator.returnCurrentDate();

    @NotNull(message = "Transaction type cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @NotNull(message = "Transferred amount cannot be null")
    @Digits(integer = 52, fraction = 2)
    @Column(name = "amount", precision = 52, scale = 2)
    private BigDecimal transferredAmount;

    @NotNull(message = "currency cannot be null")
    @Column(nullable = false)
    private String currency = "EUR";

    @NotNull(message = "Source account id cannot be null")
    @Column(name = "source_account_id", nullable = false)
    private Long sourceAccountId;

    @NotNull(message = "Source account type cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "source_account_type", nullable = false)
    private ReturnType sourceAccountType;

    @NotNull(message = "Primary Owner cannot be null")
    @OneToOne
    @JoinColumn(name = "source_account_primary_owner_id", nullable = false)
    private AccountHolder sourceAccountPrimaryOwner;

    @NotNull(message = "Target account id cannot be null")
    @JoinColumn(name = "target_account_id", nullable = false)
    private Long targetAccountId;

    @NotNull(message = "Target account type cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "target_account_type", nullable = false)
    private ReturnType targetAccountType;

    @NotNull(message = "Primary Owner cannot be null")
    @OneToOne
    @JoinColumn(name = "target_account_primary_owner_id", nullable = false)
    private AccountHolder targetAccountPrimaryOwner;

    public Transaction(TransactionType transactionType, BigDecimal transferredAmount, Long sourceAccountId, ReturnType sourceAccountType, AccountHolder sourceAccountPrimaryOwner, Long targetAccountId, ReturnType targetAccountType, AccountHolder targetAccountPrimaryOwner) {
        this.transactionType = transactionType;
        this.transferredAmount = transferredAmount;
        this.sourceAccountId = sourceAccountId;
        this.sourceAccountType = sourceAccountType;
        this.sourceAccountPrimaryOwner = sourceAccountPrimaryOwner;
        this.targetAccountId = targetAccountId;
        this.targetAccountType = targetAccountType;
        this.targetAccountPrimaryOwner = targetAccountPrimaryOwner;
    }

    public Transaction(TransactionType transactionType, BigDecimal transferredAmount, Long sourceAccountId, ReturnType sourceAccountType, AccountHolder sourceAccountPrimaryOwner, Long targetAccountId, ReturnType targetAccountType, AccountHolder targetAccountPrimaryOwner, String currency) {
        this.transactionType = transactionType;
        this.transferredAmount = transferredAmount;
        this.currency = currency;
        this.sourceAccountId = sourceAccountId;
        this.sourceAccountType = sourceAccountType;
        this.sourceAccountPrimaryOwner = sourceAccountPrimaryOwner;
        this.targetAccountId = targetAccountId;
        this.targetAccountType = targetAccountType;
        this.targetAccountPrimaryOwner = targetAccountPrimaryOwner;
    }

    public Transaction(LocalDateTime transactionDate, TransactionType transactionType, BigDecimal transferredAmount, Long sourceAccountId, ReturnType sourceAccountType, AccountHolder sourceAccountPrimaryOwner, Long targetAccountId, ReturnType targetAccountType, AccountHolder targetAccountPrimaryOwner, String currency) {
        this.transactionDate = transactionDate;
        this.transactionType = transactionType;
        this.transferredAmount = transferredAmount;
        this.sourceAccountId = sourceAccountId;
        this.sourceAccountType = sourceAccountType;
        this.sourceAccountPrimaryOwner = sourceAccountPrimaryOwner;
        this.targetAccountId = targetAccountId;
        this.targetAccountType = targetAccountType;
        this.targetAccountPrimaryOwner = targetAccountPrimaryOwner;
        this.currency = currency;
    }
}