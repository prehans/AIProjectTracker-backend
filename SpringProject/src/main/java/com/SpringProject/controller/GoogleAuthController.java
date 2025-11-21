package com.SpringProject.controller;


import com.SpringProject.Entity.User;
import com.SpringProject.Repository.UserRepository;
import com.SpringProject.service.GoogleTokenVerifier;
import com.SpringProject.service.JWTService;
import com.SpringProject.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173") // React app origin
public class GoogleAuthController {

    @Autowired
    private GoogleTokenVerifier googleTokenVerifier;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private UserService userService;

    private final UserRepository userRepository;
    public GoogleAuthController(UserRepository userRepository) {
        this.userRepository = userRepository;

    }


    @PostMapping("/google-register")
    public ResponseEntity<Map<String,String>>  RegisterWithGoogle(@RequestBody Map<String, String> body) throws Exception {
        String token = body.get("token");

        var payload = googleTokenVerifier.verifyToken(token);
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        User registeredUser = userService.registerUser(email, name);


        String Authtoken = jwtService.generateToken(email).getBody();


        Map<String, String> response = new HashMap<>();
        response.put("token", Authtoken);
        response.put("message", "Register Successful");
        response.put("timestamp", Instant.now().toString());
        response.put("email", email);
        response.put("name", name);


        response.put("jwt", String.valueOf(Authtoken));
        System.out.println("jwt "+ Authtoken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/google-login")
    public ResponseEntity<Map<String,String>> LoginWithGoogle(@RequestBody Map<String, String> body) throws Exception {
        String token = body.get("token");

        var payload = googleTokenVerifier.verifyToken(token);
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        User user = userRepository.findByUsername(email);
        if (user == null) {
            throw new UsernameNotFoundException("User Not Found");
        }


        String Authtoken = jwtService.generateToken(email).getBody();

        Map<String, String> response = new HashMap<>();
        response.put("token", Authtoken);
        response.put("message", "Login Successful");
        response.put("timestamp", Instant.now().toString());
        response.put("email", email);
        response.put("name", name);

        response.put("jwt", String.valueOf(Authtoken));
        System.out.println("jwt "+ Authtoken);
        return ResponseEntity.ok(response);
    }

}
