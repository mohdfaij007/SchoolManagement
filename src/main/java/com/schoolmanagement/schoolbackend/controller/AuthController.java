package com.schoolmanagement.schoolbackend.controller;

import com.schoolmanagement.schoolbackend.model.User;
import com.schoolmanagement.schoolbackend.payload.request.RegisterRequest;
import com.schoolmanagement.schoolbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.schoolmanagement.schoolbackend.payload.request.LoginRequest;
import com.schoolmanagement.schoolbackend.payload.response.JwtResponse;
import com.schoolmanagement.schoolbackend.security.jwt.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/auth") // Matches the permitAll() rule in SecurityConfig
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest registerRequest) {
        // Basic validation
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            return new ResponseEntity<>("Username is already taken!", HttpStatus.BAD_REQUEST);
        }

        // Create new user's account
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword())); // HASH THE PASSWORD!
        user.setRole(registerRequest.getRole() != null ? registerRequest.getRole() : "STUDENT"); // Default role

        userRepository.save(user);

        return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);
    }
    
    
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        
        // 1. Authenticate the user (Spring Security handles checking password matches)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // 2. Set the authentication in the context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Generate JWT Token
        String jwt = jwtUtils.generateJwtToken(authentication);

        // 4. Get User Details (to send back role/username)
        // Note: Casting to UserDetails is safe because authentication succeeded
        org.springframework.security.core.userdetails.UserDetails userDetails = 
            (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();

        // 5. Return Response
        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), "STUDENT")); // Hardcoded role for now
    }
}