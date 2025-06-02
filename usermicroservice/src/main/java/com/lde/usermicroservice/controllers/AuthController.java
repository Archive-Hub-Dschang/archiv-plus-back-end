package com.lde.usermicroservice.controllers;

import com.lde.usermicroservice.dto.LoginUserRequestDTO; // Créez ce DTO
import com.lde.usermicroservice.dto.LoginResponseDTO; // Créez ce DTO
import com.lde.usermicroservice.dto.RegisterUserRequestDTO;
import com.lde.usermicroservice.security.JwtTokenProvider;
import com.lde.usermicroservice.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;

    // Endpoint de connexion
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> authenticateUser(@RequestBody LoginUserRequestDTO loginRequest) {
        // Authentifie l'utilisateur via Spring Security
        // C'est AuthenticationManager qui appellera votre CustomUserDetailsService et PasswordEncoder
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // Définit l'objet Authentication dans le SecurityContext (pour la durée de cette requête)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Génère le token JWT
        String jwt = tokenProvider.generateToken(authentication);

        // Retourne le token au client
        return ResponseEntity.ok(new LoginResponseDTO(jwt));
    }

    // Endpoint d'inscription
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterUserRequestDTO registrationDto) {
        try {
            // Appelle le service métier pour enregistrer l'utilisateur (sans générer de token ici)
            userService.registerNewUser(registrationDto);
            // Si l'inscription réussit, vous pouvez soit demander au client de se connecter
            // soit le connecter automatiquement et lui renvoyer un token ici.
            // Pour l'instant, on renvoie un statut OK.
            return new ResponseEntity<>("Utilisateur enregistré avec succès !", HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Gérez les erreurs d'enregistrement (ex: email déjà utilisé)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
