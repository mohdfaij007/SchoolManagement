package com.schoolmanagement.schoolbackend.security;

import com.schoolmanagement.schoolbackend.model.User;
import com.schoolmanagement.schoolbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
//import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

//        // Note: For a real app, you would map User.role to GrantedAuthority
//        return new org.springframework.security.core.userdetails.User(
//                user.getUsername(),
//                user.getPassword(), // The stored (hashed) password
//                Collections.emptyList() // Simple list of authorities (roles) for now
//        );
        
        return UserDetailsImpl.build(user);
    }
    
    
	
    
}