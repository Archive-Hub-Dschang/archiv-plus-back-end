package com.lde.usermicroservice.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Data
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long userId;
    private String examId;

}
