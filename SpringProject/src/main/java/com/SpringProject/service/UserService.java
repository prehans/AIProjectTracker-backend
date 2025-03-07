//package com.SpringProject.service;
//
//import com.SpringProject.Entity.User;
//import com.SpringProject.Repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//@Service
//public class UserService {
//
//    @Autowired
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//
//
////    private PasswordEncoder passwordEncoder;
//    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
//        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
//    }
//    public List<User> getUser(){
//        List<User> users = userRepository.findAll();
//        return users;
//    }
//
//    public User registerUser(User user){
//        user.setPassword(passwordEncoder.encode((user.getPassword())));
//        User newUser = userRepository.save(user);
//        return newUser;
//    }
//
//    public User getUserByUsername(String username){
//        User user= userRepository.findByUsername(username);
//        if (user == null) {
//            System.out.println("User Not Found");
//
//        }
//        return user;
//    }
//}
//
//
//

package com.SpringProject.service;

import com.SpringProject.Entity.User;
import com.SpringProject.Repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getUser() {
        return userRepository.findAll();
    }

//    public User registerUser(User user) {
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        return userRepository.save(user);
//    }
public User registerUser(String username, String password) {
    if (userRepository.findByUsername(username) != null) {
        throw new RuntimeException("Username already taken");
    }

    User newUser = new User();
    newUser.setUsername(username);
    newUser.setPassword(passwordEncoder.encode(password));

    return userRepository.save(newUser);
}

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User Not Found");
        }
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
//                .authorities("USER")
                .build();
    }
}

