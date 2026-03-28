package com.rejunk.controller;

import com.rejunk.domain.model.Notification;
import com.rejunk.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/user/{userId}")
    public List<Notification> getNotificationsByUser(@PathVariable UUID userId) {
        return notificationService.getNotificationsByUser(userId);
    }

    @PatchMapping("/{id}/read")
    public Notification markAsRead(@PathVariable UUID id) {
        return notificationService.markAsRead(id);
    }
}