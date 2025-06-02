package com.lde.api_gateway.filter;

import com.lde.api_gateway.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component

public class JwtAuthenticationFilter implements WebFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // Si l'en-tête Authorization n'est pas présent ou ne commence pas par "Bearer ",
        // on laisse la requête passer. Spring Security (après ce filtre) la gérera comme non-authentifiée.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);

        // Si le token n'est pas valide (signature, expiration, etc.), on laisse passer.
        // Spring Security (après ce filtre) rejettera l'accès.
        if (!jwtService.isTokenValid(token)) {
            return chain.filter(exchange);
        }

        String username = jwtService.extractUsername(token);
        List<GrantedAuthority> authorities = jwtService.getAuthorities(token);

        // Si le username est null ou les autorités sont vides, on considère l'authentification comme invalide.
        // Spring Security (après ce filtre) rejettera l'accès.
        if (username == null || authorities.isEmpty()) {
            return chain.filter(exchange);
        }

        // --- PARTIE CRUCIALE : AJOUT DES EN-TÊTES À LA REQUÊTE TRANSFERÉE AU MICROSERVICE ---
        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .header("X-User-Email", username) // Ajoute l'e-mail de l'utilisateur
                // Convertit la liste des autorités en une chaîne séparée par des virgules pour l'en-tête
                .header("X-User-Roles", authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(",")))
                .build();

        // On crée un nouvel échange avec la requête modifiée.
        ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();

        // On crée l'objet d'authentification pour le contexte de sécurité de la GATEWAY.
        // Cela permet à Spring Security de la Gateway de marquer la requête comme authentifiée.
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                new User(username, "", authorities),
                null,
                authorities
        );

        // On continue la chaîne de filtres avec la requête modifiée et le contexte de sécurité de la Gateway.
        return chain.filter(modifiedExchange) // Utilisez modifiedExchange ici
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(
                        Mono.just(new SecurityContextImpl(authToken))
                ));
    }
}