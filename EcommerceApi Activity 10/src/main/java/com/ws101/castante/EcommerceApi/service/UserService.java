package com.ws101.castante.EcommerceApi.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ws101.castante.EcommerceApi.model.Role;
import com.ws101.castante.EcommerceApi.model.User;
import com.ws101.castante.EcommerceApi.repository.UserRepository;

/**
 * Service class for user-related business logic.
 * 
 * Handles user registration, password encoding, and user management operations.
 * This service interacts with UserRepository for database persistence.
 * 
 * @author Gabriel Castante
 * @version 1.0
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs the UserService with required dependencies.
     * 
     * @param userRepository the user repository for database operations
     * @param passwordEncoder the password encoder for hashing passwords
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user in the system.
     * 
     * Validates that username and email are unique, hashes the password,
     * and assigns the default USER role to new users.
     * 
     * @param username the username for the new user
     * @param email the email for the new user
     * @param password the plaintext password to be hashed
     * @param role the role to assign to the user (defaults to USER if null)
     * @return the saved User entity
     * @throws IllegalArgumentException if username or email already exists
     */
    public User registerUser(String username, String email, String password, Role role) {
        username = username == null ? null : username.trim();
        email = email == null ? null : email.trim();
        password = password == null ? null : password.trim();

        // Validate uniqueness
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        // Create new user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        // Assign role
        Set<Role> roles = new HashSet<>();
        roles.add(role != null ? role : Role.USER);
        user.setRoles(roles);

        // Save and return
        return userRepository.save(user);
    }

    /**
     * Finds a user by their username.
     * 
     * @param username the username to search for
     * @return the User if found, null otherwise
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    /**
     * Finds a user by their email.
     * 
     * @param email the email to search for
     * @return the User if found, null otherwise
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    /**
     * Checks if a username exists in the system.
     * 
     * @param username the username to check
     * @return true if the username exists, false otherwise
     */
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Checks if an email exists in the system.
     * 
     * @param email the email to check
     * @return true if the email exists, false otherwise
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
