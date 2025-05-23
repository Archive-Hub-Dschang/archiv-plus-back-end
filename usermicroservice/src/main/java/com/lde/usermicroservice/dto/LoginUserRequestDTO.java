package com.lde.usermicroservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginUserRequestDTO {
    @NotBlank(message = "L'email est requis.")
    @Email(message = "L'email doit être valide.")
    private String email;

    @NotBlank(message = "Le mot de passe est requis.")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères.")
    private String password;
}
