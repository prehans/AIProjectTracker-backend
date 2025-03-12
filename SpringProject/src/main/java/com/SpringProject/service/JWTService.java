package com.SpringProject.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

//import static jdk.jfr.internal.EventWriterKey.getKey;

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
    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
//        Claims claims =Jwts.parser()

//        .setSigningKey(secretKey)
//                .parseClaimsJws(token)
//                .getBody();
//        return claims.getSubject();
        return extractClaim(token, Claims::getSubject);

    }
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
//                .setSigningKey(secretKey)
                .build()
                .parseSignedClaims(token)
//                .parseClaimsJws(token)
                .getPayload();
//                .getBody();
    }

    public boolean validateToken(String token, String userName) {
       String username = extractUsername(token);
        return (username.equals(userName) && !isTokenExpired(token));


    }

    private boolean isTokenExpired(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getKey())
//                .setSigningKey(secretKey)
                .build()
                .parseSignedClaims(token)
//                .parseClaimsJws(token)
                .getPayload();
//                .getBody();
        return claims.getExpiration().before(new Date());
    }
}
