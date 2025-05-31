package com.lde.usermicroservice.security;

import com.lde.usermicroservice.models.User; // Assurez-vous d'importer le bon package pour votre entité User
import com.lde.usermicroservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections; // Pour List.of() ou Collections.singletonList()
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Recherche de l'utilisateur par e-mail
        User user = userRepository.findByEmail(email) // Assurez-vous d'avoir cette méthode dans UserRepository
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // 2. Conversion du rôle unique de l'entité User en une liste de GrantedAuthority
        // Puisque 'roles' est un enum RoleName directement, nous le convertissons en chaîne.
        List<SimpleGrantedAuthority> authorities = List.of(
                // Convertit l'enum RoleName en sa représentation String (par exemple, ADMIN -> "ADMIN")
                // ATTENTION AUX PRÉFIXES: Si vos règles Spring Security (comme @PreAuthorize("hasRole('ADMIN')"))
                // attendent le préfixe "ROLE_", vous devez l'ajouter ici:
                // new SimpleGrantedAuthority("ROLE_" + user.getRoles().name())
                new SimpleGrantedAuthority("ROLE_" + user.getRoles().name())
        );

        // 3. Création de l'objet UserDetails de Spring Security
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),    // L'e-mail de l'utilisateur servira d'identifiant principal
                user.getPassword(), // Le mot de passe haché de l'utilisateur
                authorities         // La liste d'autorités (rôles)
        );
    }
}