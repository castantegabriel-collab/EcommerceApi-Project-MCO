package com.ws101.castante.EcommerceApi.model;

/**
 * Enumeration of user roles in the ecommerce system.
 * 
 * Defines the different roles users can have for authorization purposes.
 * Each role determines what actions a user is allowed to perform.
 * 
 * @author Gabriel Castante
 * @version 1.0
 */
public enum Role {
    /**
     * Administrator role with full access to all operations.
     * Can manage products, categories, users, and view all orders.
     */
    ADMIN,

    /**
     * Regular user/customer role.
     * Can browse products and place orders.
     */
    USER,

    /**
     * Seller role for managing their own products.
     * Can create, update, and delete their own products.
     */
    SELLER
}
