package com.lde.usermicroservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateCollaborateurRequestDTO {
    private String username;
    private String email;
    private String role;
}
