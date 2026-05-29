package com.ws101.castante.EcommerceApi.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ws101.castante.EcommerceApi.exception.ProductNotFoundException;
import com.ws101.castante.EcommerceApi.model.Product;
import com.ws101.castante.EcommerceApi.repository.ProductRepository;

/**
 * Service class for product-related business logic.
 * 
 * This class handles all product operations by delegating to the ProductRepository
 * and CategoryRepository for database persistence. All products are now stored in
 * the database instead of in-memory.
 * 
 * @author Gabriel Castante
 * @version 2.0
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Constructs the ProductService with required repository dependencies.
     * 
     * @param productRepository the product repository for database operations
     */
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Retrieves all products from the database.
     * 
     * @return a list of all available products
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Finds a product by its unique identifier.
     * 
     * @param id the product identifier
     * @return the matching product
     * @throws ProductNotFoundException if no product exists with the given id
     */
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found."));
    }

    /**
     * Creates a new product and persists it to the database.
     * 
     * @param product the product to create
     * @return the created product with an assigned id from the database
     * @throws IllegalArgumentException if product is null
     */
    public Product createProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product payload is required.");
        }
        return productRepository.save(product);
    }

    /**
     * Replaces an existing product with new data and persists the changes.
     * 
     * @param id the id of the product to replace
     * @param product the replacement product payload
     * @return the updated product
     * @throws ProductNotFoundException if the product cannot be found
     */
    public Product updateProduct(Long id, Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product payload is required.");
        }

        Product existingProduct = getProductById(id);
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setStockQuantity(product.getStockQuantity());
        existingProduct.setImageUrl(product.getImageUrl());
        return productRepository.save(existingProduct);
    }

    /**
     * Partially updates product fields and persists the changes.
     * 
     * @param id the id of the product to update
     * @param updates a map of field names to new values
     * @return the updated product
     * @throws ProductNotFoundException if the product cannot be found
     * @throws IllegalArgumentException if any update value is invalid
     */
    public Product partialUpdateProduct(Long id, Map<String, Object> updates) {
        Product product = getProductById(id);
        if (updates == null || updates.isEmpty()) {
            throw new IllegalArgumentException("At least one field must be provided for patch update.");
        }

        updates.forEach((field, value) -> {
            switch (field) {
                case "name" -> {
                    String nameValue = asString(value);
                    if (nameValue == null || nameValue.isBlank()) {
                        throw new IllegalArgumentException("Product name cannot be blank.");
                    }
                    product.setName(nameValue);
                }
                case "description" -> product.setDescription(asString(value));
                case "price" -> product.setPrice(asDouble(value));
                case "stockQuantity" -> product.setStockQuantity(asInteger(value));
                case "imageUrl" -> product.setImageUrl(asString(value));
                default -> throw new IllegalArgumentException("Unknown product field: " + field);
            }
        });
        return productRepository.save(product);
    }

    /**
     * Deletes a product by its identifier from the database.
     * 
     * @param id the id of the product to delete
     * @throws ProductNotFoundException if the product cannot be found
     */
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    /**
     * Filters products by search criteria.
     * Supports filtering by category name or price range.
     * 
     * @param filterType the type of filter (category, price_between, price_less_than, price_greater_than)
     * @param filterValue the filter value (comma-separated for price ranges)
     * @return matching products or all products when no filter is provided
     */
    public List<Product> filterProducts(String filterType, String filterValue) {
        if (filterType == null || filterType.isBlank() || filterValue == null || filterValue.isBlank()) {
            return getAllProducts();
        }

        return switch (filterType.toLowerCase()) {
            case "category" -> productRepository.findByCategoryName(filterValue);
            case "price_between" -> {
                String[] parts = filterValue.split(",");
                if (parts.length != 2) {
                    throw new IllegalArgumentException("price_between filter requires two comma-separated values: minPrice,maxPrice");
                }
                Double minPrice = parseDouble(parts[0].trim());
                Double maxPrice = parseDouble(parts[1].trim());
                yield productRepository.findByPriceBetween(minPrice, maxPrice);
            }
            case "price_less_than" -> {
                Double maxPrice = parseDouble(filterValue.trim());
                yield productRepository.findByPriceBetween(0.0, maxPrice);
            }
            case "price_greater_than" -> {
                Double minPrice = parseDouble(filterValue.trim());
                yield productRepository.findByPriceBetween(minPrice, Double.MAX_VALUE);
            }
            default -> getAllProducts();
        };
    }

    /**
     * Finds all products in a specific category by category name.
     * 
     * @param categoryName the name of the category
     * @return a list of products in the specified category
     */
    public List<Product> findProductsByCategory(String categoryName) {
        return productRepository.findByCategoryName(categoryName);
    }

    /**
     * Finds all products within a specific price range.
     * 
     * @param minPrice the minimum price (inclusive)
     * @param maxPrice the maximum price (inclusive)
     * @return a list of products within the price range
     */
    public List<Product> findProductsByPriceRange(Double minPrice, Double maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    /**
     * Finds all products that are currently in stock.
     * 
     * @return a list of products with stock quantity > 0
     */
    public List<Product> findAvailableProducts() {
        return productRepository.findAvailableProducts();
    }

    // ========== Helper Methods ==========

    /**
     * Converts a value to a String, safely handling null values.
     * 
     * @param value the value to convert
     * @return the string representation or null
     */
    private String asString(Object value) {
        if (value == null) {
            return null;
        }
        return value.toString().trim();
    }

    /**
     * Converts a value to a Double with validation.
     * Ensures price is greater than 0.
     * 
     * @param value the value to convert
     * @return the double value
     * @throws IllegalArgumentException if value is not a valid positive number
     */
    private Double asDouble(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Price value is required.");
        }
        if (value instanceof Number number) {
            double converted = number.doubleValue();
            if (converted <= 0) {
                throw new IllegalArgumentException("Price must be greater than 0.");
            }
            return converted;
        }
        try {
            double converted = Double.parseDouble(value.toString());
            if (converted <= 0) {
                throw new IllegalArgumentException("Price must be greater than 0.");
            }
            return converted;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Price must be a valid number.");
        }
    }

    /**
     * Converts a value to an Integer with validation.
     * Ensures stock quantity is non-negative.
     * 
     * @param value the value to convert
     * @return the integer value
     * @throws IllegalArgumentException if value is not a valid non-negative integer
     */
    private Integer asInteger(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Stock quantity is required.");
        }
        if (value instanceof Number number) {
            int converted = number.intValue();
            if (converted < 0) {
                throw new IllegalArgumentException("Stock quantity must be non-negative.");
            }
            return converted;
        }
        try {
            int converted = Integer.parseInt(value.toString());
            if (converted < 0) {
                throw new IllegalArgumentException("Stock quantity must be non-negative.");
            }
            return converted;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Stock quantity must be a valid integer.");
        }
    }

    /**
     * Safely parses a string to a Double.
     * 
     * @param value the string value to parse
     * @return the parsed double value
     * @throws IllegalArgumentException if parsing fails
     */
    private Double parseDouble(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Filter value for price must be a valid number.");
        }
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Filter value for price must be a valid number.");
        }
    }
}
