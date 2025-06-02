package com.lde.usermicroservice.controllers;

import com.lde.usermicroservice.dto.*;
import com.lde.usermicroservice.models.User;
import com.lde.usermicroservice.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        String role =regiseterUserRequestDto.Role.User;
        return ResponseEntity.ok(new AuthResponseDTO(userService.registerUser(username, email, password)));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> loginUser(@RequestBody LoginUserRequestDTO loginUserRequestDTO) {
        String email = loginUserRequestDTO.getEmail();
        String password = loginUserRequestDTO.getPassword();
        return ResponseEntity.ok(new AuthResponseDTO(userService.loginUser(email, password)));
    }

    @PostMapping("/collaborateurs")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<CollaborateurResponseDTO> addCollaborateur(@RequestBody CreateCollaborateurRequestDTO dto) {
        CollaborateurResponseDTO collaborateur = userService.addCollaborateur(dto);
        return new ResponseEntity<>(collaborateur, HttpStatus.CREATED);
    }

    @GetMapping("/collaborateurs")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<List<User>> getCollaborateurs() {
        return ResponseEntity.ok(userService.getCollaborateurs());
    }

    @DeleteMapping("/collaborateurs/{id}")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<Void> deleteCollaborateur(@PathVariable Long id) {
        userService.deleteCollaborateur(id);
        return ResponseEntity.noContent().build();
    }
}
