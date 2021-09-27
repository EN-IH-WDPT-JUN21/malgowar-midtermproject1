package com.ironhack.midtermproject1.dao;

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
//@AllArgsConstructor
@NoArgsConstructor
@Table(name = "admins")
public class Admin extends User{
    @NotNull(message = "First name cannot be null")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotNull(message = "Last name cannot be null")
    @Column(name = "last_name", nullable = false)
    private String lastName;
}
