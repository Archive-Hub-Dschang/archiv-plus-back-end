package com.lde.api_gateway.config;

import com.lde.api_gateway.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter; // Importation pour ajouter le filtre
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;

@Configuration
@EnableWebFluxSecurity // Active la sécurité pour Spring WebFlux
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Désactive CSRF pour les APIs sans état
                .authorizeExchange(exchanges -> exchanges
                        // Permet l'accès non authentifié aux chemins d'authentification
                        .pathMatchers("/api/auth/register", "/api/auth/login","/api/exams/**").permitAll()
                        // Toutes les autres requêtes doivent être authentifiées
                        .anyExchange().authenticated()
                )
          .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION);



        return http.build();
    }
}