package com.ws101.castante.EcommerceApi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import com.ws101.castante.EcommerceApi.security.JwtUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ws101.castante.EcommerceApi.dto.ApiResponseDto;
import com.ws101.castante.EcommerceApi.dto.LoginRequestDto;
import com.ws101.castante.EcommerceApi.dto.RegisterRequestDto;
import com.ws101.castante.EcommerceApi.model.Role;
import com.ws101.castante.EcommerceApi.model.User;
import com.ws101.castante.EcommerceApi.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * REST controller for authentication and authorization endpoints.
 * 
 * Handles user registration, login, and logout operations using session-based authentication.
 * All endpoints use HTTPS sessions (cookies) for maintaining authenticated state.
 * 
 * @author Gabriel Castante
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    /**
     * Constructs the AuthController with required dependencies.
     * 
     * @param userService the user service for user management
     * @param authenticationManager the authentication manager for manual authentication
     */
    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Registers a new user in the system.
     * 
     * Validates the registration data, ensures passwords match,
     * and checks for duplicate username/email before creating the user.
     * 
     * Endpoint: POST /api/v1/auth/register
     * 
     * @param registerRequest the registration details (validated)
     * @return success response with created user information
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto> register(@Valid @RequestBody RegisterRequestDto registerRequest) {
        try {
            // Validate passwords match
            if (!registerRequest.password().equals(registerRequest.confirmPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponseDto(false, "Passwords do not match", null));
            }

            // Register user with default USER role
            User user = userService.registerUser(
                    registerRequest.username(),
                    registerRequest.email(),
                    registerRequest.password(),
                    Role.USER
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponseDto("User registered successfully", user.getUsername()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponseDto(false, e.getMessage(), null));
        }
    }

    /**
     * Authenticates a user and creates a session.
     * 
     * Validates credentials and creates a session upon successful authentication.
     * The JSESSIONID cookie is automatically set by the server in the HTTP response.
     * 
     * Endpoint: POST /api/v1/auth/login
     * 
     * @param loginRequest the login credentials (validated)
     * @param request the HTTP request (for session creation)
     * @return success response if authentication succeeds
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest, HttpServletRequest request) {
        try {
            // Attempt authentication
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.username(),
                            loginRequest.password()
                    )
            );

            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT for the authenticated user
            Object principal = authentication.getPrincipal();
            String token = jwtUtil.generateToken((org.springframework.security.core.userdetails.UserDetails) principal);

            return ResponseEntity.ok(new ApiResponseDto("Login successful", java.util.Map.of("token", token)));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto(false, "Invalid username or password", null));
        }
    }

    /**
     * Logs out the current user by invalidating the session.
     * 
     * Endpoint: POST /api/v1/auth/logout
     * 
     * @param request the HTTP request (for session invalidation)
     * @return success response
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDto> logout(HttpServletRequest request) {
        // For JWT stateless auth there's no server-side session to invalidate.
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new ApiResponseDto("Logout successful"));
    }
}
