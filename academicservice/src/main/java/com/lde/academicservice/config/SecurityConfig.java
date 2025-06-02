package com.lde.academicservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // <-- IMPORTANT
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity // Active les fonctionnalités de sécurité web de Spring
@EnableMethodSecurity // Active les annotations de sécurité au niveau des méthodes (@PreAuthorize, @PostAuthorize)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Désactive CSRF pour les APIs REST sans état
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Pas de gestion de session
                .authorizeHttpRequests(authorize -> authorize
                        // Les endpoints /public sont accessibles à tous (même non authentifiés)
                        .requestMatchers("/api/exams/**").permitAll()
                        // Toutes les autres requêtes nécessitent une authentification
                        .anyRequest().authenticated()
                )
                // Ajoute notre filtre personnalisé pour lire les en-têtes de la Gateway
                .addFilterBefore(new UserHeaderAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Filtre personnalisé qui extrait les informations d'authentification et de rôle
     * des en-têtes HTTP 'X-User-Email' et 'X-User-Roles' envoyés par la Gateway.
     * Ces informations sont ensuite utilisées pour créer le contexte de sécurité Spring.
     */
    public static class UserHeaderAuthenticationFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {

            String userEmail = request.getHeader("X-User-Email");
            String userRoles = request.getHeader("X-User-Roles");

            if (userEmail != null && userRoles != null) {
                List<GrantedAuthority> authorities = Arrays.stream(userRoles.split(","))
                        .map(roleString -> {
                            // Nettoie les espaces et met en majuscules pour normaliser le rôle (ex: "collaborator" -> "COLLABORATOR")
                            String baseRole = roleString.trim().toUpperCase();
                            // Ajoute le préfixe "ROLE_" pour correspondre à l'utilisation de hasRole() dans @PreAuthorize
                            // Ex: "COLLABORATOR" devient "ROLE_COLLABORATOR"
                            return new SimpleGrantedAuthority("ROLE_" + baseRole);
                        })
                        .collect(Collectors.toList());

                // Crée un objet UserDetails simple pour représenter l'utilisateur authentifié
                UserDetails userDetails = new org.springframework.security.core.userdetails.User(userEmail, "", authorities);

                // Crée et place le token d'authentification dans le SecurityContextHolder
                // Cela rend l'utilisateur authentifié et ses rôles disponibles pour les vérifications de sécurité.
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
        }
    }
}