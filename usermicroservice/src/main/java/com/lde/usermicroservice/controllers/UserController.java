package com.lde.usermicroservice.controllers;

import com.lde.usermicroservice.dto.AuthResponseDTO;
import com.lde.usermicroservice.dto.LoginUserRequestDTO;
import com.lde.usermicroservice.dto.RegisterUserRequestDTO;
import com.lde.usermicroservice.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> registerUser(@RequestBody RegisterUserRequestDTO regiseterUserRequestDto) {
        String username = regiseterUserRequestDto.getUsername();
        String email = regiseterUserRequestDto.getEmail();
        String password = regiseterUserRequestDto.getPassword();
        return ResponseEntity.ok(new AuthResponseDTO(username, email, userService.registerUser(username, email, password)));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> loginUser(@RequestBody LoginUserRequestDTO loginUserRequestDTO) {
        String email = loginUserRequestDTO.getEmail();
        String password = loginUserRequestDTO.getPassword();
        return ResponseEntity.ok(userService.loginUser(email, password));
    }

    @GetMapping("/protected/test")
    public ResponseEntity<?> testProtectedRoute() {
        return ResponseEntity.ok().body("{\"message\": \"Accès autorisé via JWT ✅\"}");
    }
}