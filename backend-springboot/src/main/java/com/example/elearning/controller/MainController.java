package com.example.elearning.controller;

import com.example.elearning.entity.User;
import com.example.elearning.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class MainController {

    @Autowired
    private UserRepository userRepository;

    // -------------------- Home Endpoint --------------------
    @GetMapping("/home")
    public String home() {
        return "Welcome to the E-Learning Platform!";
    }

    // -------------------- User APIs --------------------
    @PostMapping("/users/signup")
    public ResponseEntity<String> signup(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        } else {
            user.setRole("STUDENT");  // Set default role
            userRepository.save(user);
            return ResponseEntity.ok("User registered successfully");
        }
    }

    @PostMapping("/users/login")
    public ResponseEntity<String> login(@RequestBody User loginRequest) {
        Optional<User> optionalUser = userRepository.findByUsername(loginRequest.getUsername());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
        User user = optionalUser.get();
        if (user.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

}
