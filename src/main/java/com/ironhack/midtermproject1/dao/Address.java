package com.ironhack.midtermproject1.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "account_holders_addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Street cannot be null")
    @Column(nullable = false)
    private String street;

    @NotNull(message = "House number cannot be null")
    @Column(name = "house_number", nullable = false)
    private String houseNumber;

    @Column(name = "local_number")
    private String localNumber;

    @NotNull(message = "City cannot be null")
    @Column(nullable = false)
    private String city;

    @NotNull(message = "Country cannot be null")
    @Column(nullable = false)
    private String country;

    @Column(name = "postal_code")
    private String postalCode;

    public Address(String street, String houseNumber, String localNumber, String city, String country, String postalCode) {
        this.street = street;
        this.houseNumber = houseNumber;
        this.localNumber = localNumber;
        this.city = city;
        this.country = country;
        this.postalCode = postalCode;
    }
}
