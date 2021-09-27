package com.ironhack.midtermproject1.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "account_holders")
public class AccountHolder extends User{
    @NotNull(message = "First Name cannot be null")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotNull(message = "Last Name cannot be null")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @NotNull(message = "Primary Address cannot be null")
    @OneToOne()
    @JoinColumn(name = "address_id", nullable = false)
    private Address primaryAddress;

    @Email(regexp=".*@.*\\..*", message = "Email should be valid")
    private String email;

    public AccountHolder(String username, String password, boolean enabled, String firstName, String lastName, LocalDate birthDate, Address primaryAddress, String email) {
        super(username, password, enabled);
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.primaryAddress = primaryAddress;
        this.email = email;
    }
}
