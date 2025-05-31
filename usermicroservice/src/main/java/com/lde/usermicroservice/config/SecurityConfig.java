package com.lde.usermicroservice.config;

import com.lde.usermicroservice.security.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Active @PreAuthorize, @PostAuthorize etc.
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailService customUserDetailService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // Permet l'accès public aux chemins d'authentification
                        // même si la Gateway les gère, c'est une bonne pratique de défense en profondeur
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                        // Toutes les autres requêtes nécessitent une authentification
                        .anyRequest().authenticated()
                )
                // AJOUT DU FILTRE POUR LIRE LES EN-TÊTES DE LA GATEWAY
                // Ce filtre va authentifier l'utilisateur basé sur les en-têtes X-User-*
                .addFilterBefore(new UserHeaderAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // NOUVELLE CLASSE DE FILTRE POUR LIRE LES EN-TÊTES DE LA GATEWAY
    // Vous pouvez la déclarer comme une classe interne statique ou dans un fichier séparé
    public static class UserHeaderAuthenticationFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {

            String userEmail = request.getHeader("X-User-Email");
            String userRoles = request.getHeader("X-User-Roles"); // Les rôles sont une chaîne séparée par des virgules

            // Si les en-têtes sont présents, cela signifie que la Gateway a authentifié l'utilisateur
            if (userEmail != null && userRoles != null) {
                // Convertir la chaîne de rôles en Collection<GrantedAuthority>
                List<GrantedAuthority> authorities = Arrays.stream(userRoles.split(","))
                        // Très important: Si vos rôles JWT sont "ADMIN" mais que Spring Security attend "ROLE_ADMIN",
                        // décommentez la ligne ci-dessous:
                        .map(role -> "ROLE_" + role.trim())
                        .map(SimpleGrantedAuthority::new) // Assurez-vous que les rôles sont au format attendu par Spring Security
                        .collect(Collectors.toList());

                // Créer un objet UserDetails simple pour le contexte de sécurité
                // Le mot de passe n'est pas nécessaire ici car l'authentification a déjà eu lieu sur la Gateway
                UserDetails userDetails = new org.springframework.security.core.userdetails.User(userEmail, "", authorities);

                // Créer l'objet d'authentification et le définir dans le SecurityContextHolder
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
        }
    }
}