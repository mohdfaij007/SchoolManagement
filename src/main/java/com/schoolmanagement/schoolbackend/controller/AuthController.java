package com.schoolmanagement.schoolbackend.controller;

import com.schoolmanagement.schoolbackend.model.SchoolProfile;
import com.schoolmanagement.schoolbackend.model.User;
import com.schoolmanagement.schoolbackend.payload.request.RegisterRequest;
import com.schoolmanagement.schoolbackend.repository.SchoolProfileRepository;
import com.schoolmanagement.schoolbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.schoolmanagement.schoolbackend.payload.request.LoginRequest;
import com.schoolmanagement.schoolbackend.payload.response.JwtResponse;
import com.schoolmanagement.schoolbackend.security.UserDetailsImpl;
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
    private SchoolProfileRepository schoolProfileRepository;

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
     // We fetch School ID 1. If it doesn't exist yet, it stays null safely.
        SchoolProfile defaultSchool = schoolProfileRepository.findById(1L).orElse(null);
        user.setSchoolProfile(defaultSchool);
        
        userRepository.save(user);

        return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);
    }
    
    
 // Keep your existing imports and registerUser method the same...

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        
        // 1. Authenticate the user 
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // 2. Set the authentication in the context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Generate JWT Token
        String jwt = jwtUtils.generateJwtToken(authentication);

        // 4. Get User Details (Cast to our custom UserDetailsImpl so we can access getRole())
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // 5. Return Response WITH THE REAL ROLE
        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), userDetails.getRole(), userDetails.getSchoolId())); 
    }
}