package com.rejunk.dto.order;

import com.rejunk.domain.enums.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    private UUID id;
    private BigDecimal totalAmount;
    private OrderStatus orderStatus;
    private Instant createdAt;
    private List<OrderItemResponse> items;
    private UUID buyerId;
    private String status;
    private double amount;


}