package com.ws101.castante.EcommerceApi.exception;

/**
 * Exception thrown when a product cannot be found by its identifier.
 */
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String message) {
        super(message);
    }
}
