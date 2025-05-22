package com.lde.usermicroservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.*;

@Data
@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Admin extends User {

}