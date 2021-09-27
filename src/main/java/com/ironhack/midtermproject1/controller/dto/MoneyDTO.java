package com.ironhack.midtermproject1.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Currency;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MoneyDTO {
    @ColumnDefault("0.00")
    @Digits(integer = 52, fraction = 2)
    @Column(precision=52, scale=2)
    BigDecimal balance;

    @NotNull(message = "Currency cannot be null")
    @Column(nullable = false)
    Currency currency;// = CurrencyUnit.EUR;
}
