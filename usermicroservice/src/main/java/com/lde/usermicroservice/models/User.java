package com.lde.usermicroservice.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@Entity(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RoleName role ;

}
