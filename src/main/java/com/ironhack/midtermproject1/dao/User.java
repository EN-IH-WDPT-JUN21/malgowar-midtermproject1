package com.ironhack.midtermproject1.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "username_id")
    private Long id;

    @NotNull(message = "Username cannot be null")
    @Column(nullable = false)
    private String username;

    @NotNull(message = "Password cannot be null")
    @Size(min=7)
    @Column(nullable = false)
    private String password;

    @ColumnDefault("true")
    private boolean enabled = true;

    @OneToMany(/*mappedBy = "user", */fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Role> roles;

    public User(String username, String password, boolean enabled) {
        this.username = username;
        this.password = password;
        this.enabled = enabled;
    }
}
