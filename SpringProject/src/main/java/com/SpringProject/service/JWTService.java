package com.SpringProject.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JWTService {

    private String secretKey = "";
    public JWTService() throws NoSuchAlgorithmException {
        KeyGenerator KeyGen = KeyGenerator.getInstance("HmacSHA256");
        SecretKey key = KeyGen.generateKey();
        secretKey = Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public ResponseEntity<String> generateToken(String username) {
        Map<String , Object> claims = new HashMap<>();
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
                 return ResponseEntity.ok(token);
    }

}
