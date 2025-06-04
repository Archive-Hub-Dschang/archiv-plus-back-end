package com.lde.paymentmicroservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto  {
    private Long id;
    private String username;
    private String email;

}
