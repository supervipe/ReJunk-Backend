package com.rejunk.service;

import com.rejunk.domain.enums.NotificationType;
import com.rejunk.domain.model.Notification;
import com.rejunk.domain.model.User;
import com.rejunk.dto.notification.NotificationResponse;
import com.rejunk.repository.NotificationRepository;
import com.rejunk.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(UUID.randomUUID());
    }

    // CREATE NOTIFICATION

    @Test
    void createNotification_shouldCreateSuccessfully() {
        UUID userId = user.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(i -> i.getArgument(0));

        Notification result = notificationService.createNotification(
                userId,
                NotificationType.ITEM_SOLD,
                "Item sold successfully"
        );

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(NotificationType.ITEM_SOLD, result.getType());
        assertEquals("Item sold successfully", result.getMessage());
        assertFalse(result.isRead());

        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void createNotification_shouldThrowIfUserNotFound() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> notificationService.createNotification(
                        userId,
                        NotificationType.ITEM_SOLD,
                        "Test message"
                ));

        assertEquals("User not found", ex.getMessage());
    }

    // GET NOTIFICATIONS

    @Test
    void getNotificationsByUser_shouldReturnList() {
        UUID userId = user.getId();

        Notification notification = Notification.builder()
                .id(UUID.randomUUID())
                .user(user)
                .type(NotificationType.ITEM_SOLD)
                .message("Item sold")
                .read(false)
                .createdAt(Instant.now())
                .build();

        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(List.of(notification));

        List<NotificationResponse> responses =
                notificationService.getNotificationsByUser(userId);

        assertEquals(1, responses.size());
        assertEquals(notification.getMessage(), responses.get(0).getMessage());
        assertEquals(notification.getType().name(), responses.get(0).getType());
    }

    @Test

    void markAsRead_shouldUpdateSuccessfully() {
        UUID notificationId = UUID.randomUUID();

        Notification notification = new Notification();
        notification.setId(notificationId);
        notification.setUser(user);
        notification.setType(NotificationType.ITEM_SOLD); //
        notification.setMessage("Test message");
        notification.setRead(false);

        when(notificationRepository.findById(notificationId))
                .thenReturn(Optional.of(notification));
        when(notificationRepository.save(notification))
                .thenReturn(notification);

        NotificationResponse response =
                notificationService.markAsRead(notificationId);

        assertTrue(notification.isRead());
        assertTrue(response.isRead());

        verify(notificationRepository).save(notification);
    }

    @Test
    void markAsRead_shouldThrowIfNotFound() {
        UUID notificationId = UUID.randomUUID();

        when(notificationRepository.findById(notificationId))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> notificationService.markAsRead(notificationId));

        assertEquals("Notification not found", ex.getMessage());
    }
}