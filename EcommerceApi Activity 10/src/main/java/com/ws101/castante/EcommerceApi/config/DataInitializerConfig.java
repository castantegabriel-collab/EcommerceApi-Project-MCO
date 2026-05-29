package com.ws101.castante.EcommerceApi.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ws101.castante.EcommerceApi.model.Category;
import com.ws101.castante.EcommerceApi.model.Product;
import com.ws101.castante.EcommerceApi.model.Role;
import com.ws101.castante.EcommerceApi.model.User;
import com.ws101.castante.EcommerceApi.repository.CategoryRepository;
import com.ws101.castante.EcommerceApi.repository.ProductRepository;
import com.ws101.castante.EcommerceApi.repository.UserRepository;

import java.util.HashSet;
import java.util.Set;

// import java.util.Optional;

/**
 * Data initialization configuration for seeding the database with sample products, categories, and users.
 * 
 * This configuration runs on application startup and populates the database with sample data
 * only if the database is empty. This is useful for development and testing purposes.
 * 
 * @author Gabriel Castante
 * @version 2.0
 */
@Configuration
public class DataInitializerConfig {

    /**
     * CommandLineRunner bean that initializes the database with sample data.
     * 
     * The data is only inserted if no products exist in the database (empty database check).
     * This ensures the sample data is not duplicated on subsequent application restarts.
     * 
     * @param categoryRepository the category repository for database operations
     * @param productRepository the product repository for database operations
     * @param userRepository the user repository for database operations
     * @param passwordEncoder the password encoder for hashing passwords
     * @return a CommandLineRunner that executes the initialization
     */
    @Bean
    public CommandLineRunner initializeDatabase(
            CategoryRepository categoryRepository, 
            ProductRepository productRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // Only initialize if no products exist
            if (productRepository.count() == 0) {
                System.out.println("Database is empty. Initializing with sample data...");
                initializeUsers(userRepository, passwordEncoder);
                initializeCategories(categoryRepository);
                initializeProducts(categoryRepository, productRepository);
                System.out.println("Sample data initialization complete!");
            } else {
                System.out.println("Database already contains data. Skipping initialization.");
            }
        };
    }

    /**
     * Initializes sample users for testing.
     * 
     * Creates sample users with different roles:
     * - admin/admin123 (ADMIN role)
     * - shopper/shopper123 (USER role)
     * - seller/seller123 (SELLER role)
     * 
     * @param userRepository the user repository
     * @param passwordEncoder the password encoder
     */
    private void initializeUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        // Admin user
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@ecommerce.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(Role.ADMIN);
            admin.setRoles(adminRoles);
            userRepository.save(admin);
            System.out.println("Created admin user: admin");
        }

        // Regular user (shopper)
        if (userRepository.findByUsername("shopper").isEmpty()) {
            User shopper = new User();
            shopper.setUsername("shopper");
            shopper.setEmail("shopper@ecommerce.com");
            shopper.setPassword(passwordEncoder.encode("shopper123"));
            Set<Role> shopperRoles = new HashSet<>();
            shopperRoles.add(Role.USER);
            shopper.setRoles(shopperRoles);
            userRepository.save(shopper);
            System.out.println("Created regular user: shopper");
        }

        // Seller user
        if (userRepository.findByUsername("seller").isEmpty()) {
            User seller = new User();
            seller.setUsername("seller");
            seller.setEmail("seller@ecommerce.com");
            seller.setPassword(passwordEncoder.encode("seller123"));
            Set<Role> sellerRoles = new HashSet<>();
            sellerRoles.add(Role.SELLER);
            seller.setRoles(sellerRoles);
            userRepository.save(seller);
            System.out.println("Created seller user: seller");
        }
    }

    /**
     * Initializes sample product categories.
     * 
     * @param categoryRepository the category repository
     */
    private void initializeCategories(CategoryRepository categoryRepository) {
        String[] categoryNames = {"Electronics", "Sportswear", "Home Appliances", "Accessories", "Fitness", "Home"};
        for (String name : categoryNames) {
            if (categoryRepository.findByName(name).isEmpty()) {
                Category category = new Category();
                category.setName(name);
                categoryRepository.save(category);
                System.out.println("Created category: " + name);
            }
        }
    }

    /**
     * Initializes sample products with category relationships.
     * 
     * @param categoryRepository the category repository
     * @param productRepository the product repository
     */
    private void initializeProducts(CategoryRepository categoryRepository, ProductRepository productRepository) {
        // Sample product data: name, description, price, category, stock quantity, image URL
        Object[][] sampleProducts = {
                {"Laptop", "High-performance laptop for work and gaming", 1299.99, "Electronics", 10, "https://via.placeholder.com/250x200?text=Laptop"},
                {"Wireless Mouse", "Ergonomic wireless mouse with long battery life", 29.99, "Electronics", 35, "https://via.placeholder.com/250x200?text=Mouse"},
                {"Smartphone", "Latest generation smartphone with 128GB storage", 799.99, "Electronics", 18, "https://via.placeholder.com/250x200?text=Smartphone"},
                {"Running Shoes", "Lightweight running shoes for everyday training", 64.50, "Sportswear", 24, "https://via.placeholder.com/250x200?text=Running+Shoes"},
                {"Coffee Maker", "Programmable coffee maker with 12-cup capacity", 49.95, "Home Appliances", 12, "https://via.placeholder.com/250x200?text=Coffee+Maker"},
                {"Backpack", "Durable travel backpack with laptop compartment", 39.99, "Accessories", 27, "https://via.placeholder.com/250x200?text=Backpack"},
                {"Yoga Mat", "Non-slip yoga mat for home workouts", 22.00, "Fitness", 40, "https://via.placeholder.com/250x200?text=Yoga+Mat"},
                {"Bluetooth Speaker", "Portable Bluetooth speaker with rich bass", 54.99, "Electronics", 15, "https://via.placeholder.com/250x200?text=Speaker"},
                {"Desk Lamp", "LED desk lamp with adjustable brightness", 19.99, "Home", 28, "https://via.placeholder.com/250x200?text=Desk+Lamp"},
                {"Water Bottle", "Insulated stainless steel water bottle", 18.25, "Accessories", 50, "https://via.placeholder.com/250x200?text=Water+Bottle"}
        };

        for (Object[] data : sampleProducts) {
            String productName = (String) data[0];
            String description = (String) data[1];
            Double price = (Double) data[2];
            String categoryName = (String) data[3];
            Integer stockQuantity = (Integer) data[4];
            String imageUrl = (String) data[5];

            // Check if product already exists
            if (productRepository.findByName(productName).isEmpty()) {
                // Get or create category
                Category category = categoryRepository.findByName(categoryName)
                        .orElseGet(() -> {
                            Category newCategory = new Category();
                            newCategory.setName(categoryName);
                            return categoryRepository.save(newCategory);
                        });

                // Create and save product
                Product product = new Product();
                product.setName(productName);
                product.setDescription(description);
                product.setPrice(price);
                product.setCategory(category);
                product.setStockQuantity(stockQuantity);
                product.setImageUrl(imageUrl);

                productRepository.save(product);
                System.out.println("Created product: " + productName + " in category: " + categoryName);
            }
        }
    }
}
