package com.ws101.castante.EcommerceApi.dto;

/**
 * Data Transfer Object for API responses.
 * 
 * Generic response payload for successful operations.
 * 
 * @author Gabriel Castante
 * @version 1.0
 */
public record ApiResponseDto(
    /**
     * Indicates success or failure of the operation.
     */
    boolean success,

    /**
     * Message describing the result of the operation.
     */
    String message,

    /**
     * Optional data payload associated with the response.
     */
    Object data
) {
    /**
     * Convenience constructor for success responses with data.
     */
    public ApiResponseDto(String message, Object data) {
        this(true, message, data);
    }

    /**
     * Convenience constructor for simple success responses.
     */
    public ApiResponseDto(String message) {
        this(true, message, null);
    }
}
