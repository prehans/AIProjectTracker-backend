package com.SpringProject.controller;

import com.SpringProject.Entity.User;
import com.SpringProject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;
    @GetMapping("/login")
     public String login(){
         return "Login Sucessfully";
     }

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
//        return userService.loadUserByUsername(username);
//     }

}
