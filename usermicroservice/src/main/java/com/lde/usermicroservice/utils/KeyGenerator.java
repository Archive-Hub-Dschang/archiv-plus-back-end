package com.lde.usermicroservice.utils;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;

public class KeyGenerator {
    public static void main(String[] args) {
        // Génère une clé sécurisée de 512 bits pour HS512 (64 octets)
        String secretKey = Base64.getEncoder().encodeToString(Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded());
        System.out.println("Clé JWT Base64 générée : " + secretKey);
    }
}