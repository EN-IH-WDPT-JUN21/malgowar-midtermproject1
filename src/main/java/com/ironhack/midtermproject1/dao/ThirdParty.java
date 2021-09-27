package com.ironhack.midtermproject1.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "third_party")
public class ThirdParty extends User{
    @NotNull(message = "Hashed key cannot be null")
    @Column(name = "hashed_key", nullable = false)
    private Integer hashedKey;

    @NotNull(message = "First Name cannot be null")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotNull(message = "Last Name cannot be null")
    @Column(name = "last_name", nullable = false)
    private String lastName;

}
