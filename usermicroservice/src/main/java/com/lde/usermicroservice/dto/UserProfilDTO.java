package com.lde.usermicroservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfilDTO {
    private Long id; // L'ID est maintenant un Long
    private String username;
    private String email;
}
