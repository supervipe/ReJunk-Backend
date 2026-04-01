package com.rejunk.domain.model;

import com.rejunk.domain.enums.NotificationType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    @Test
    void testNotificationProperties() {
        // Create a related User
        User user = new User();

        // Create a Notification
        Notification notification = new Notification();
        UUID notificationId = UUID.randomUUID();
        notification.setId(notificationId);
        notification.setUser(user);
        notification.setType(NotificationType.PAYMENT_CONFIRMED);
        notification.setMessage("Your payment is successful");
        notification.setRead(true);

        // Simulate @PrePersist
        notification.setCreatedAt(Instant.now());

        // Assertions
        assertEquals(notificationId, notification.getId());
        assertEquals(user, notification.getUser());
        assertEquals(NotificationType.   PAYMENT_CONFIRMED, notification.getType());
        assertEquals("Your payment is successful", notification.getMessage());
        assertTrue(notification.isRead());
        assertNotNull(notification.getCreatedAt());
    }

    @Test
    void testDefaultReadValue() {
        Notification notification = new Notification();
        assertFalse(notification.isRead());
    }

    @Test
    void testOnCreateSetsCreatedAt() {
        Notification notification = new Notification();
        notification.onCreate();
        assertNotNull(notification.getCreatedAt());
        assertTrue(notification.getCreatedAt().isBefore(Instant.now().plusSeconds(1)));
    }
}