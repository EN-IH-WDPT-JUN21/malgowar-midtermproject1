package com.ironhack.midtermproject1.dao;

import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.RoundingMode;
import java.math.BigDecimal;
import java.util.Currency;

//@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class Money {

    private static final Currency USD = Currency.getInstance("USD");
    private static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_EVEN;

    @NotNull(message = "Currency cannot be null")
    @Column(nullable = false)
    private Currency currency;

    @ColumnDefault("0.00")
    @Digits(integer = 52, fraction = 2)
    @Column(precision=52, scale=2)
    private BigDecimal balance;

    /**
     * Class constructor specifying amount, currency, and rounding
     **/

    public Money(BigDecimal balance, Currency currency, RoundingMode rounding) {
        this.currency = currency;
        setBalance(balance.setScale(currency.getDefaultFractionDigits(), rounding));
    }

    /**
     * Class constructor specifying amount, and currency. Uses default RoundingMode HALF_EVEN.
     **/
    public Money(BigDecimal balance, Currency currency) {
        this(balance, currency, DEFAULT_ROUNDING);
    }

    /**
     * Class constructor specifying amount. Uses default RoundingMode HALF_EVEN and default currency USD.
     **/
    public Money(BigDecimal amount) {
        this(amount, USD, DEFAULT_ROUNDING);
    }

    public BigDecimal increaseAmount(Money money) {
        setBalance(this.balance.add(money.balance));
        return this.balance;
    }

    public BigDecimal increaseAmount(BigDecimal addAmount) {
        setBalance(this.balance.add(addAmount));
        return this.balance;
    }

    public BigDecimal decreaseAmount(Money money) {
        setBalance(this.balance.subtract(money.getBalance()));
        return this.balance;
    }

    public BigDecimal decreaseAmount(BigDecimal addAmount) {
        setBalance(this.balance.subtract(addAmount));
        return this.balance;
    }

    public Currency getCurrency() {
        return this.currency;
    }

    public BigDecimal getBalance() {
        return this.balance;
    }

    public void setBalance(BigDecimal amount) {
        this.balance = amount;
    }

    public String toString() {
        return getCurrency().getSymbol() + " " + getBalance();
    }
}