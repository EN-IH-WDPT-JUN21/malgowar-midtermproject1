package com.ironhack.midtermproject1.dao;

import com.ironhack.midtermproject1.enums.ReturnType;
import com.ironhack.midtermproject1.utils.dataValidator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

@Getter
@Setter
//@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public abstract class Account extends Money{
    @ColumnDefault("0.00")
    @Digits(integer = 52, fraction = 2)
    @Column(precision=52, scale=2)
    BigDecimal balance;

    @NotNull(message = "Primary Owner cannot be null")
    @OneToOne
    @JoinColumn(name = "primary_owner_id",  nullable = false)
    AccountHolder primaryOwner;

    @OneToOne(optional = true)
    @JoinColumn(name = "secondary_owner_id")
    AccountHolder secondaryOwner;

    @ColumnDefault("40.00")
    @Digits(integer = 10, fraction = 2)
    @Column(name = "penalty_fee", precision=10, scale=2)
    final BigDecimal penaltyFee = new BigDecimal(40);

    @NotNull(message = "Creation date cannot be null")
    @Column(name = "creation_date", nullable = false)
    LocalDateTime creationDate = dataValidator.returnCurrentDate();

    public Account(BigDecimal balance, Currency currency, AccountHolder primaryOwner, AccountHolder secondaryOwner, LocalDateTime creationDate) {
        super(balance, currency);
        this.primaryOwner = primaryOwner;
        this.secondaryOwner = secondaryOwner;
        this.creationDate = creationDate;
    }

    public abstract ReturnType getReturnType();
}
