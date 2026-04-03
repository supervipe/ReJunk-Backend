package com.rejunk.service;

import com.rejunk.domain.enums.NotificationType;
import com.rejunk.domain.model.Notification;
import com.rejunk.domain.model.User;
import com.rejunk.dto.notification.NotificationResponse;
import com.rejunk.repository.NotificationRepository;
import com.rejunk.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Notification createNotification(UUID userId, NotificationType type, String message) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .message(message)
                .read(false)
                .build();

        return notificationRepository.save(notification);
    }

    public List<NotificationResponse> getNotificationsByUser(UUID userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);

        List<NotificationResponse> responses = new ArrayList<>();

        for (Notification notification : notifications) {
            responses.add(toResponse(notification));
        }

        return responses;
    }

    @Transactional
    public NotificationResponse markAsRead(UUID notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setRead(true);

        Notification saved = notificationRepository.save(notification);

        return toResponse(saved);
    }

    private NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType().name())
                .message(notification.getMessage())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}