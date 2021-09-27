package com.ironhack.midtermproject1.dao;

import com.ironhack.midtermproject1.enums.AccountStatus;
import com.ironhack.midtermproject1.enums.ReturnType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

import static com.ironhack.midtermproject1.enums.ReturnType.STUDENT_CHECKING;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "student_checking_accounts")
public class StudentChecking extends Account{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Secret key cannot be null")
    @Column(name = "secret_key", nullable = false)
    private String secretKey;

    @NotNull(message = "Status cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    public StudentChecking(BigDecimal balance, Currency currency, AccountHolder primaryOwner, AccountHolder secondaryOwner, LocalDateTime creationDate, String secretKey, AccountStatus status) {
        super(balance, currency, primaryOwner, secondaryOwner, creationDate);
        this.secretKey = secretKey;
        this.status = status;
    }

    public ReturnType getReturnType(){
        return STUDENT_CHECKING;
    }
}
