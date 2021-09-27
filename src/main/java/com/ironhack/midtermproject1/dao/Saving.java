package com.ironhack.midtermproject1.dao;

import com.ironhack.midtermproject1.enums.AccountStatus;
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
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

import static com.ironhack.midtermproject1.enums.ReturnType.SAVING;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "saving_accounts")
public class Saving extends Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Secret key cannot be null")
    @Column(name = "secret_key", nullable = false)
    private String secretKey;

    @DecimalMin(value = "100.00", message = "Minimum balance should not be less than 100.00")
    @DecimalMax(value = "1000.00", message = "Minimum balance should not be greater than 1000.00")
    @ColumnDefault("1000.00")
    @Digits(integer = 52, fraction = 2)
    @Column(name = "minimum_balance", precision=52, scale=2)
    private BigDecimal minimumBalance = new BigDecimal(1000);

    @DecimalMax(value = "0.5", message = "Interest rate should not be greater than 0.5")
    @ColumnDefault("0.0025")
    @Digits(integer = 7, fraction = 4)
    @Column(name = "interest_rate", precision=7, scale=4)
    private BigDecimal interestRate = new BigDecimal(0.0025);

    @Column(name = "last_interest_date")
    private LocalDateTime additionLastInterestDate  = super.getCreationDate();

    @NotNull(message = "Status cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    public ReturnType getReturnType(){
        return SAVING;
    }

    public Saving(BigDecimal balance, Currency currency, AccountHolder primaryOwner, AccountHolder secondaryOwner, LocalDateTime creationDate, String secretKey, BigDecimal minimumBalance, BigDecimal interestRate, LocalDateTime additionLastInterestDate, AccountStatus status) {
        super(balance, currency, primaryOwner, secondaryOwner, creationDate);
        this.secretKey = secretKey;
        this.minimumBalance = minimumBalance;
        this.interestRate = interestRate;
        this.additionLastInterestDate = additionLastInterestDate;
        this.status = status;
    }

    //for object with default minimumBalance and interestRate
    public Saving(BigDecimal balance, Currency currency, AccountHolder primaryOwner, AccountHolder secondaryOwner, LocalDateTime creationDate, String secretKey, LocalDateTime additionLastInterestDate, AccountStatus status) {
        super(balance, currency, primaryOwner, secondaryOwner, creationDate);
        this.secretKey = secretKey;
        this.additionLastInterestDate = additionLastInterestDate;
        this.status = status;
    }
}
