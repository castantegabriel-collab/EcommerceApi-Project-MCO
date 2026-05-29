package com.ws101.castante.EcommerceApi.exception;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Global exception handler that returns consistent API error responses across the application.
 * 
 * Handles both application-specific exceptions (e.g., ProductNotFoundException) and
 * Spring/database exceptions (e.g., DataIntegrityViolationException) with appropriate
 * HTTP status codes and JSON error responses.
 * 
 * @author Gabriel Castante
 * @version 2.0
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles ProductNotFoundException - returns 404 NOT FOUND.
     * 
     * @param ex the exception thrown
     * @param request the HTTP request
     * @return error response with 404 status
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleProductNotFound(ProductNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    /**
     * Handles validation exceptions - returns 400 BAD REQUEST.
     * Formats field-level validation errors.
     * 
     * @param ex the validation exception
     * @param request the HTTP request
     * @return error response with 400 status and field errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return buildResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    /**
     * Handles illegal argument exceptions - returns 400 BAD REQUEST.
     * 
     * @param ex the exception
     * @param request the HTTP request
     * @return error response with 400 status
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    /**
     * Handles database integrity violations - returns 400 BAD REQUEST.
     * Examples: duplicate unique key, foreign key constraint violations.
     * 
     * @param ex the exception
     * @param request the HTTP request
     * @return error response with 400 status
     */
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        Throwable cause = ex.getCause();
        String message = "Data integrity violation: " + (cause != null ? cause.getMessage() : "constraint violation");
        return buildResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    /**
     * Handles JPA object retrieval failures - returns 404 NOT FOUND.
     * Thrown when an entity cannot be found in the database.
     * 
     * @param ex the exception
     * @param request the HTTP request
     * @return error response with 404 status
     */
    @ExceptionHandler(JpaObjectRetrievalFailureException.class)
    public ResponseEntity<ApiErrorResponse> handleJpaObjectRetrievalFailure(JpaObjectRetrievalFailureException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "Resource not found in the database.", request);
    }

    /**
     * Handles empty result data access exceptions - returns 404 NOT FOUND.
     * Thrown when a delete operation fails because the entity doesn't exist.
     * 
     * @param ex the exception
     * @param request the HTTP request
     * @return error response with 404 status
     */
    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ApiErrorResponse> handleEmptyResultDataAccess(EmptyResultDataAccessException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "Resource not found for the requested operation.", request);
    }

    /**
     * Handles authentication exceptions - returns 401 UNAUTHORIZED.
     * Thrown when user authentication fails (invalid credentials).
     * 
     * @param ex the exception
     * @param request the HTTP request
     * @return error response with 401 status
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Authentication failed: " + ex.getMessage(), request);
    }

    /**
     * Handles access denied exceptions - returns 403 FORBIDDEN.
     * Thrown when user lacks required permissions/roles.
     * 
     * @param ex the exception
     * @param request the HTTP request
     * @return error response with 403 status
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, "Access denied: insufficient permissions", request);
    }

    /**
     * Catches all other exceptions - returns 500 INTERNAL SERVER ERROR.
     * This is a fallback for any unhandled exceptions.
     * 
     * @param ex the exception
     * @param request the HTTP request
     * @return error response with 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneralException(Exception ex, HttpServletRequest request) {
        logger.error("Unexpected error occurred", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.", request);
    }

    /**
     * Builds a consistent API error response object.
     * 
     * @param status the HTTP status code
     * @param message the error message
     * @param request the HTTP request
     * @return response entity with ApiErrorResponse body
     */
    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String message, HttpServletRequest request) {
        ApiErrorResponse body = new ApiErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        return new ResponseEntity<>(body, status);
    }
}
