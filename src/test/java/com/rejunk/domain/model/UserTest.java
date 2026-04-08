package com.rejunk.domain.model;

import com.rejunk.domain.enums.AccountStatus;
import com.rejunk.domain.enums.UserRole;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserProperties() {
        // Create a user
        User user = new User();
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        user.setFullName("Evelyn Johnson");
        user.setEmail("evelyn@example.com");
        user.setPhone("1234567890");
        user.setPasswordHash("hashed_password");
        user.setRole(UserRole.CUSTOMER);
        user.setStatus(AccountStatus.ACTIVE);

        // Simulate @PrePersist
        user.setCreatedAt(Instant.now());

        // Assertions
        assertEquals(userId, user.getId());
        assertEquals("Evelyn Johnson", user.getFullName());
        assertEquals("evelyn@example.com", user.getEmail());
        assertEquals("1234567890", user.getPhone());
        assertEquals("hashed_password", user.getPasswordHash());
        assertEquals(UserRole.CUSTOMER, user.getRole());
        assertEquals(AccountStatus.ACTIVE, user.getStatus());
        assertNotNull(user.getCreatedAt());

        // Lists should be initialized
        assertNotNull(user.getCollectionRequests());
        assertNotNull(user.getOrders());
        assertNotNull(user.getNotifications());

        // Lists should be empty initially
        assertTrue(user.getCollectionRequests().isEmpty());
        assertTrue(user.getOrders().isEmpty());
        assertTrue(user.getNotifications().isEmpty());
    }

    @Test
    void testAddToLists() {
        User user = new User();

        // Simulate adding a CollectionRequest
        CollectionRequest request = new CollectionRequest();
        user.getCollectionRequests().add(request);
        assertEquals(1, user.getCollectionRequests().size());

        // Simulate adding an Order
        Order order = new Order();
        user.getOrders().add(order);
        assertEquals(1, user.getOrders().size());

        // Simulate adding a Notification
        Notification notification = new Notification();
        user.getNotifications().add(notification);
        assertEquals(1, user.getNotifications().size());
    }
}