package com.rejunk.dto.notification;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
public class NotificationResponse {

    private UUID id;
    private String type;
    private String message;
    private boolean read;
    private Instant createdAt;
}
