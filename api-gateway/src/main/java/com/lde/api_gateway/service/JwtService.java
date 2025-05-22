package com.lde.api_gateway.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    // La clé doit être suffisamment longue (au moins 256 bits pour HS256)
    private final String SECRET_KEY = "a8Ld9YfGqU7xvRQzM4eTiPjBsX1wEnClZg3mVu6KtR0AhDbJWpNyoF2cMEHbLaTXrVZnsfOY9GQiK7mtlURcAeWJPdXkCyoMFgvN6zqLD3Rj9HpT5EsuXYwb8ZgKNtxvMiFLAWh1oe7cV0rBQdGkMXUfTpyI4NbmRWsa93VTOKf6zqjYELCAhZlPb2XM7goJDwNxtBKs63a1CmVlHY9TZrLGJ0NRqWtEehpdCvfQKzyiJMuORAX4FgWS8bm53cEjrKhvdaNYPlHoLz2xTMVG";


    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public List<GrantedAuthority> getAuthorities(String token) {
        Claims claims = extractAllClaims(token);
        List<String> roles = claims.get("roles", List.class);
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

    }
}
