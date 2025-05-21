package com.lde.usermicroservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Contributor extends User {
    private String registrationNumber;

}