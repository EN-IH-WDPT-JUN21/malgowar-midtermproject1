package com.ironhack.midtermproject1.dao;

import com.ironhack.midtermproject1.enums.AccountStatus;
import com.ironhack.midtermproject1.enums.ReturnType;
import lombok.AllArgsConstructor;
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

import static com.ironhack.midtermproject1.enums.ReturnType.CHECKING;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "checking_accounts")
public class Checking extends Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Secret key cannot be null")
    @Column(name = "secret_key", nullable = true)
    private String secretKey;

    @ColumnDefault("250.00")
    @Digits(integer = 52, fraction = 2)
    @Column(name = "minimum_balance", precision=52, scale=2)
    private BigDecimal minimumBalance = new BigDecimal(250);

    @ColumnDefault("12")
    @Digits(integer = 52, fraction = 2)
    @Column(name = "monthly_maintenance_fee",precision=52, scale=2)
    private BigDecimal monthlyMaintenanceFee = new BigDecimal(12);

    @NotNull(message = "Status cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @Column(name = "charge_mainfee_last_date")
    private LocalDateTime chargeFeeLastDate = super.getCreationDate();;

    //FIXME: to delete
    public ReturnType getReturnType(){
        return CHECKING;
    }

    //constructor for object with default minimumBalance and monthlyMaintenanceFee and chargeFeeLastDate
    public Checking(BigDecimal balance, Currency currency, AccountHolder primaryOwner, AccountHolder secondaryOwner, LocalDateTime creationDate, String secretKey, AccountStatus status) {
        super(balance, currency, primaryOwner, secondaryOwner, creationDate);
        this.secretKey = secretKey;
        this.status = status;
    }

    //constructor for object with default minimumBalance and monthlyMaintenanceFee
    public Checking(BigDecimal balance, Currency currency, AccountHolder primaryOwner, AccountHolder secondaryOwner, LocalDateTime creationDate, String secretKey, AccountStatus status, LocalDateTime chargeFeeLastDate) {
        super(balance, currency, primaryOwner, secondaryOwner, creationDate);
        this.secretKey = secretKey;
        this.status = status;
        this.chargeFeeLastDate = chargeFeeLastDate;
    }
}
