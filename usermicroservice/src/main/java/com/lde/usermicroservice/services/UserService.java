package com.lde.usermicroservice.services;

import com.lde.usermicroservice.dto.CollaborateurResponseDTO;
import com.lde.usermicroservice.dto.CreateCollaborateurRequestDTO;
import com.lde.usermicroservice.models.RoleName;
import com.lde.usermicroservice.models.User;
import com.lde.usermicroservice.repositories.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.security.Key;
import java.util.*;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public final String secrets = "a8Ld9YfGqU7xvRQzM4eTiPjBsX1wEnClZg3mVu6KtR0AhDbJWpNyoF2cMEHbLaTXrVZnsfOY9GQiK7mtlURcAeWJPdXkCyoMFgvN6zqLD3Rj9HpT5EsuXYwb8ZgKNtxvMiFLAWh1oe7cV0rBQdGkMXUfTpyI4NbmRWsa93VTOKf6zqjYELCAhZlPb2XM7goJDwNxtBKs63a1CmVlHY9TZrLGJ0NRqWtEehpdCvfQKzyiJMuORAX4FgWS8bm53cEjrKhvdaNYPlHoLz2xTMVG";

    public String registerUser(String username, String email, String password) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);

        return createToken(email);
    }

    public String loginUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && bCryptPasswordEncoder.matches(password, user.getPassword())) {
            return createToken(email);
        }
        return null;
    }

    public void validateToken(String token) {
        Jwts.parser().
                setSigningKey(getSignKey()).
                build()
                .parseClaimsJws(token);
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


    public CollaborateurResponseDTO addCollaborateur(CreateCollaborateurRequestDTO dto) {
        String temporaryPassword = UUID.randomUUID().toString().substring(0, 8);
        User collaborateur = new User();
        collaborateur.setUsername(dto.getUsername());
        collaborateur.setEmail(dto.getEmail());
        collaborateur.setPassword(bCryptPasswordEncoder.encode(temporaryPassword));
        collaborateur.setRole(RoleName.Collaborateur);
        userRepository.save(collaborateur);

        return new CollaborateurResponseDTO(dto.getUsername(), dto.getEmail(), temporaryPassword);
    }

    public List<User> getCollaborateurs() {
        return userRepository.findAllByRole(RoleName.Collaborateur);
    }

    public void deleteCollaborateur(Long id) {
        userRepository.deleteById(id);
    }
}
