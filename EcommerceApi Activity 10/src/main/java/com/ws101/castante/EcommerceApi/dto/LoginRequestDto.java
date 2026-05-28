package com.ws101.castante.EcommerceApi.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for login requests.
 * 
 * Validates credentials before attempting authentication.
 * 
 * @author Gabriel Castante
 * @version 1.0
 */
public record LoginRequestDto(
    /**
     * Username for authentication.
     */
    @NotBlank(message = "Username is required")
    String username,

    /**
     * Password for authentication.
     */
    @NotBlank(message = "Password is required")
    String password
) {
    public LoginRequestDto {
        username = username == null ? null : username.trim();
        password = password == null ? null : password.trim();
    }
}
