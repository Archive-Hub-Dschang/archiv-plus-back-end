package com.lde.usermicroservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginUserRequestDTO {
    private String email;
    private String password;
}
