package com.saga.crm.controller;


import com.saga.crm.dto.UserDto;
import com.saga.crm.model.User;
import com.saga.crm.service.MailService;
import com.saga.crm.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private MailService mailService;

    @Autowired
    public AuthController(UserService userService, PasswordEncoder passwordEncoder, MailService mailService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody UserDto userDto) {
        User newUser = userService.registerUser(userDto);

//        try {
//            mailService.sendWelcomeEmail(newUser.getEmail(), newUser.getName());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserDto userDto) {
        User existingUser = userService.findUserByEmail(userDto.getEmail());

        if (existingUser != null && passwordEncoder.matches(userDto.getPassword(), existingUser.getPassword())) {
            Map<String, String> response = new HashMap<>();
            response.put("name", existingUser.getName());
            response.put("email", existingUser.getEmail());
            return ResponseEntity.ok(response);
        }

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Invalid credentials");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
}
