package com.ws101.castante.EcommerceApi.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ws101.castante.EcommerceApi.model.User;
import com.ws101.castante.EcommerceApi.repository.UserRepository;

/**
 * Custom implementation of Spring Security's UserDetailsService.
 * 
 * Loads user details from the database for authentication and authorization.
 * This service is used by Spring Security during the login process to verify
 * user credentials and populate the security context with user information.
 * 
 * @author Gabriel Castante
 * @version 1.0
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructs the CustomUserDetailsService with required dependencies.
     * 
     * @param userRepository the user repository for database operations
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads user details by username for authentication.
     * 
     * This method is called by Spring Security during the login process to retrieve
     * user information from the database. The returned UserDetails object contains
     * the username, password, and authorities (roles) needed for authentication.
     * 
     * @param username the username of the user to load
     * @return UserDetails object containing user information
     * @throws UsernameNotFoundException if no user exists with the given username
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return user;
    }
}
