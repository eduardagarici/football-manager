package com.aeduard.soccerOnline.model;


import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Email(message = "Invalid email")
    @Column(unique = true, nullable = false)
    private String email;

    @Size(min = 8, message = "Password should contain at least 8 characters")
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private UserRole role;

    @ToString.Exclude
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, optional = false)
    private Team team;
}
