package com.schoolmanagement.schoolbackend.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorAwareImpl")
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // 1. Get the authentication object from Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. If not authenticated (or anonymous), return generic "System" or null
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.of("System_Auto"); 
        }

        // 3. Return the username (e.g., "Teacher_Rahul" or "Emp_102")
        // In a real app, you might cast this to your UserDetails object to get an ID instead of a name.
        return Optional.of(authentication.getName());
    }
}