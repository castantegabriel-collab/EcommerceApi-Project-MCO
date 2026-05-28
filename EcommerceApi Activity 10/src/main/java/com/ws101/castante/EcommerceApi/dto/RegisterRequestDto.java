package com.ws101.castante.EcommerceApi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for user registration requests.
 * 
 * Validates all required fields before processing registration.
 * 
 * @author Gabriel Castante
 * @version 1.0
 */
public record RegisterRequestDto(
    /**
     * Username for the new account. Must be unique.
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    String username,

    /**
     * Email address for the new account. Must be unique and valid.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String email,

    /**
     * Password for the account. Will be hashed before storage.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    String password,

    /**
     * Confirmation of the password.
     */
    @NotBlank(message = "Password confirmation is required")
    String confirmPassword
) {
    public RegisterRequestDto {
        username = username == null ? null : username.trim();
        email = email == null ? null : email.trim();
        password = password == null ? null : password.trim();
        confirmPassword = confirmPassword == null ? null : confirmPassword.trim();
    }
}
