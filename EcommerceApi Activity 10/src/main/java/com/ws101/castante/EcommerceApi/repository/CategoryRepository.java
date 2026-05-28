package com.ws101.castante.EcommerceApi.repository;

import com.ws101.castante.EcommerceApi.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Category entity.
 * 
 * Extends JpaRepository to provide CRUD operations and custom query methods.
 * All database operations for categories are handled through this interface.
 * 
 * @author Gabriel Castante
 * @version 1.0
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Finds a category by its unique name.
     * 
     * @param name the category name to search for
     * @return an Optional containing the category if found
     */
    Optional<Category> findByName(String name);
}
