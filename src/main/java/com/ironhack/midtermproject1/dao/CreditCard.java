package com.ironhack.midtermproject1.dao;

import com.ironhack.midtermproject1.enums.ReturnType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

import static com.ironhack.midtermproject1.enums.ReturnType.CREDIT_CARD;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "credit_cards")
public class CreditCard extends Account{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DecimalMin(value = "0.1", message = "Interest rate should not be less than 0.1")
    @DecimalMax(value = "0.2", message = "Interest rate should not be greater than 0.2")
    @ColumnDefault("0.2")
    @Digits(integer = 7, fraction = 4)
    @Column(name = "interest_rate", precision=7, scale=4)
    private BigDecimal interestRate = new BigDecimal(0.2);

    @DecimalMin(value = "100.00", message = "Credit limit should not be less than 100.00")
    @DecimalMax(value = "100000.00", message = "Credit limit should not be greater than 100000.00")
    @ColumnDefault("100.00")
    @Digits(integer = 52, fraction = 2)
    @Column(name = "credit_limit", precision=52, scale=2)//, columnDefinition = "decimal(52,2) DEFAULT 100.00 CHECK(credit_limit > 1000)")
    private BigDecimal creditLimit = new BigDecimal(100.00);

    @Column(name = "last_interest_date")
    private LocalDateTime additionLastInterestDate = super.getCreationDate();

    public ReturnType getReturnType(){
        return CREDIT_CARD;
    }

    public CreditCard(BigDecimal balance, Currency currency, AccountHolder primaryOwner, AccountHolder secondaryOwner, LocalDateTime creationDate, BigDecimal interestRate, BigDecimal creditLimit, LocalDateTime additionLastInterestDate) {
        super(balance, currency, primaryOwner, secondaryOwner, creationDate);
        this.interestRate = interestRate;
        this.creditLimit = creditLimit;
        this.additionLastInterestDate = additionLastInterestDate;
    }

    //constructor for objects with default interestRate and CreditLimit
    public CreditCard(BigDecimal balance, Currency currency, AccountHolder primaryOwner, AccountHolder secondaryOwner, LocalDateTime creationDate, LocalDateTime additionLastInterestDate) {
        super(balance, currency, primaryOwner, secondaryOwner, creationDate);
        this.additionLastInterestDate = additionLastInterestDate;
    }
}
