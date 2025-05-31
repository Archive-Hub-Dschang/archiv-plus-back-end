package com.lde.usermicroservice.services;

import com.lde.usermicroservice.models.User;
import com.lde.usermicroservice.models.RoleName; // Importez RoleName
import com.lde.usermicroservice.repositories.UserRepository;
import com.lde.usermicroservice.dto.RegisterUserRequestDTO; // Créez ce DTO
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder; // Remplacez BCryptPasswordEncoder par PasswordEncoder
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Utilisez l'interface PasswordEncoder

    // Nouvelle méthode pour l'enregistrement d'un utilisateur
    public User registerNewUser(RegisterUserRequestDTO registrationDto) {
        if (userRepository.findByEmail(registrationDto.getEmail()).isPresent()) {
            throw new RuntimeException("L'e-mail est déjà utilisé !"); // Ou une exception plus spécifique
        }

        User newUser = User.builder()
                .username(registrationDto.getUsername())
                .email(registrationDto.getEmail())
                .password(passwordEncoder.encode(registrationDto.getPassword()))
                .roles(RoleName.User) // Attribue le rôle par défaut (USER)
                .build();

        return userRepository.save(newUser);
    }

    public Optional<User> getByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail);
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public User saveUser(User user) { // Méthode pour sauver des utilisateurs (utile pour l'init admin)
        return userRepository.save(user);
    }
}