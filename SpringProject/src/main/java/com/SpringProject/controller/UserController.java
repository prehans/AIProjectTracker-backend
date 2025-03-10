package com.SpringProject.controller;

import com.SpringProject.Entity.User;
import com.SpringProject.service.JWTService;
import com.SpringProject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public UserController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
    static class LoginRequest {
        public String username;
        public String password;
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username, request.password)
            );
            if (authentication.isAuthenticated()) {
                return jwtService.generateToken(request.username);
//                        ResponseEntity.ok("Login Successfully");
            } else {
                return ResponseEntity.status(401).body("Invalid credentials");
            }
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

//    @PostMapping("/login")
//    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
//        try {
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(username, password)
//            );
//            if (authentication.isAuthenticated()) {
//                return ResponseEntity.ok("Login Successfully");
//            } else {
//                return ResponseEntity.status(401).body("Invalid credentials");
//            }
//        } catch (AuthenticationException e) {
//            return ResponseEntity.status(401).body("Invalid credentials");
//        }
//    }

     @GetMapping("/allUsers")
     public List<User> allUsers(){
        return userService.getUser();
     }

//     @PostMapping("/register")
//        public String registerUser(@RequestBody User user){
//          User u = userService.registerUser(user);
//            return "User Registered Successfully "+u.getUsername()+" "+u.getPassword();
//        }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        try {
            userService.registerUser(user.getUsername(), user.getPassword());
            return ResponseEntity.ok("User registered successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//     @GetMapping("/getUserByUsername")
//     public User getUserByUsername(@RequestParam String username){
//         System.out.println("User Not Found");
//
////        return userService.loadUserByUsername(username);
//     }

}
