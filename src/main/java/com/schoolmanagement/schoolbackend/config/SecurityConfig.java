package com.schoolmanagement.schoolbackend.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.schoolmanagement.schoolbackend.security.jwt.AuthTokenFilter;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	// Define the Password Encoder
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// Define the filter Bean
	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}

	// Add this bean inside SecurityConfig class:
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	// Configure the Security Filter Chain
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				// 1. ENABLE CORS HERE (Crucial step)
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				// 1. Disable CSRF (not needed for stateless JWT APIs)
				.csrf(AbstractHttpConfigurer::disable)

				// 2. Configure session management to be stateless
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				// 3. Define authorization rules
				.authorizeHttpRequests(auth -> auth
						// Allow public access to registration and login endpoints
						.requestMatchers("/api/auth/**").permitAll().requestMatchers("/error").permitAll()
						.requestMatchers("/api/attendance/**").permitAll()
						.requestMatchers("/api/master/**").permitAll()
						.requestMatchers("/api/students/**").permitAll()
						//Give access to get from folder photo 
						.requestMatchers("/photos/**").permitAll()
						// Secure all other endpoints
						.anyRequest().authenticated());

		// 2. Add our JWT Filter BEFORE the standard
		// UsernamePasswordAuthenticationFilter
		// You'll need to import UsernamePasswordAuthenticationFilter class
		http.addFilterBefore(authenticationJwtTokenFilter(),
				org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

		// Note: We will add the JWT filter and AuthenticationManager setup later (Day
		// 4)

		return http.build();
	}

	// 2. Define the CORS configuration strictly for Spring Security
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow your Angular Frontend
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        
        // Allow all standard methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Allow the Authorization header (for the JWT) and Content-Type
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        // Allow credentials (cookies/auth headers)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}