package com.ws101.castante.EcommerceApi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration class for Spring MVC.
 * 
 * Configures Cross-Origin Resource Sharing (CORS) to allow the frontend
 * running on localhost:5500 to communicate with the backend API on localhost:8080.
 * Also maps login and registration templates to their public routes.
 * 
 * @author Gabriel Castante
 * @version 1.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Adds CORS mappings to allow cross-origin requests from the frontend.
     * 
     * Configuration details:
     * - Allowed origins: http://localhost:5500 (Live Server or other frontend port)
     * - Allowed methods: GET, POST, PUT, DELETE, PATCH, OPTIONS
     * - Allowed headers: Authorization, Content-Type
     * - Allow credentials: true (for cookie-based authentication if needed)
     * - Max age: 3600 seconds (1 hour) for preflight cache
     * 
     * @param registry the CORS registry to add mappings to
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("http://localhost:5500", "https://*.netlify.app")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type", "X-CSRF-TOKEN")
                .exposedHeaders("X-CSRF-TOKEN")
                .allowCredentials(true)
                .maxAge(3600);
        
        // Allow CORS for login endpoints
        registry.addMapping("/login")
                .allowedOrigins("http://localhost:5500")
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("Content-Type", "X-CSRF-TOKEN")
                .exposedHeaders("X-CSRF-TOKEN")
                .allowCredentials(true)
                .maxAge(3600);
        
        // Allow CORS for logout endpoint
        registry.addMapping("/logout")
                .allowedOrigins("http://localhost:5500")
                .allowedMethods("POST", "OPTIONS")
                .allowedHeaders("Content-Type", "X-CSRF-TOKEN")
                .exposedHeaders("X-CSRF-TOKEN")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/register").setViewName("register");
        registry.addViewController("/register.html").setViewName("register");
        registry.addViewController("/index.html").setViewName("index");
    }
}
