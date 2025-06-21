package com.example.demo.controller;

import com.example.demo.dto.LoginDto;
import com.example.demo.dto.LoginResponse;
import jakarta.validation.Valid;
import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth") // Base URL for authentication routes
public class AuthController {
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }


    // Home endpoint (just for testing)
    @GetMapping("/index")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Welcome to the API!");
    }

    // Register user
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserDto userDto, BindingResult result) {
        User existingUser = userService.findUserByEmail(userDto.getEmail());

        if (existingUser != null) {
            return ResponseEntity.badRequest().body("There is already an account registered with this email.");
        }

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Invalid user data.");
        }

        userService.saveUser(userDto);
        return ResponseEntity.ok("User registered successfully!");
    }

    // Get list of users
    @GetMapping("/users")
    public List<UserDto> getAllUsers() {
        System.out.println("Fetching users...");  // Debugging statement
        return userService.findAllUsers();
    }


    // Login endpoint (to be modified later for authentication)
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDto loginDto) {
        User user = userService.findUserByEmail(loginDto.getEmail());  // Plain User now

        if (user == null || !passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        LoginResponse response = new LoginResponse(user.getId(), user.getName());
        return ResponseEntity.ok(response);
    }


}