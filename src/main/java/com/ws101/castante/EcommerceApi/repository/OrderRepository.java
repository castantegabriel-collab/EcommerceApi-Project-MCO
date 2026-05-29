package com.ws101.castante.EcommerceApi.repository;

import com.ws101.castante.EcommerceApi.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Order entity.
 * 
 * Extends JpaRepository to provide CRUD operations and custom query methods.
 * All database operations for orders are handled through this interface.
 * 
 * @author Gabriel Castante
 * @version 1.0
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Finds all orders placed by a specific customer using method name-based query derivation.
     * 
     * @param customerName the name of the customer to search for
     * @return a list of orders placed by the customer
     */
    List<Order> findByCustomerName(String customerName);

    /**
     * Finds an order by its unique order ID (human-readable identifier).
     * 
     * @param orderId the order ID to search for
     * @return an Optional containing the order if found
     */
    Optional<Order> findByOrderId(String orderId);

    /**
     * Finds all orders with a specific status (e.g., "PENDING", "SHIPPED", "DELIVERED").
     * 
     * @param status the order status to filter by
     * @return a list of orders with the specified status
     */
    List<Order> findByStatus(String status);

    /**
     * Finds all orders placed within a specific date/time range using JPQL.
     * 
     * @param startDate the start date/time (inclusive)
     * @param endDate the end date/time (inclusive)
     * @return a list of orders placed within the specified date range
     */
    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate ORDER BY o.orderDate DESC")
    List<Order> findOrdersInDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
