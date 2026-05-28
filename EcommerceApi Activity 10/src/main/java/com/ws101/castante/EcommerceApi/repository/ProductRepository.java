package com.ws101.castante.EcommerceApi.repository;

import com.ws101.castante.EcommerceApi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Product entity.
 * 
 * Extends JpaRepository to provide CRUD operations and custom query methods.
 * All database operations for products are handled through this interface.
 * 
 * @author Gabriel Castante
 * @version 1.0
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Finds all products that belong to a specific category by category name.
     * Uses method name-based query derivation.
     * 
     * @param categoryName the name of the category to filter by
     * @return a list of products in the specified category
     */
    List<Product> findByCategoryName(String categoryName);

    /**
     * Finds all products within a specific price range using JPQL.
     * 
     * @param minPrice the minimum price (inclusive)
     * @param maxPrice the maximum price (inclusive)
     * @return a list of products within the specified price range
     */
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice ORDER BY p.price ASC")
    List<Product> findByPriceBetween(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);

    /**
     * Finds a product by its name.
     * 
     * @param name the product name to search for
     * @return an Optional containing the product if found
     */
    Optional<Product> findByName(String name);

    /**
     * Finds all products with stock quantity greater than zero (available for purchase).
     * 
     * @return a list of products that are in stock
     */
    @Query("SELECT p FROM Product p WHERE p.stockQuantity > 0 ORDER BY p.name ASC")
    List<Product> findAvailableProducts();
}
