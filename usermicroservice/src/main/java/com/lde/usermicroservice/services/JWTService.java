package com.lde.usermicroservice.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JWTService {

    public final String secrets = "a8Ld9YfGqU7xvRQzM4eTiPjBsX1wEnClZg3mVu6KtR0AhDbJWpNyoF2cMEHbLaTXrVZnsfOY9GQiK7mtlURcAeWJPdXkCyoMFgvN6zqLD3Rj9HpT5EsuXYwb8ZgKNtxvMiFLAWh1oe7cV0rBQdGkMXUfTpyI4NbmRWsa93VTOKf6zqjYELCAhZlPb2XM7goJDwNxtBKs63a1CmVlHY9TZrLGJ0NRqWtEehpdCvfQKzyiJMuORAX4FgWS8bm53cEjrKhvdaNYPlHoLz2xTMVG";

    public void validateToken(String token) {
        Jwts.parser().setSigningKey(getSignKey()).build().parseClaimsJws(token);
    }

    public String createToken(String token) {
        Map<String, Object> claims = new HashMap<>();
        return this.generateToken(claims, token);
    }

    public String generateToken(Map<String, Object> claims, String email) {
        return Jwts.builder().setSubject(email).signWith(getSignKey()).setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 3)).signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = secrets.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
