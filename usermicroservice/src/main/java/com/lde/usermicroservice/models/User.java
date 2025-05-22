package com.lde.usermicroservice.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@Entity(name = "users")
public class User {
    @Id
    private String id;

    @PrePersist
    public void ensureId() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

    private String username;

    @Column(unique = true)
    private String email;

    private String password;
}