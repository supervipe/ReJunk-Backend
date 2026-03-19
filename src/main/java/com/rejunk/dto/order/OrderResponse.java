package com.rejunk.dto.order;

import com.rejunk.domain.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class OrderResponse {

    private UUID id;
    private BigDecimal totalAmount;
    private OrderStatus orderStatus;
    private Instant createdAt;
    private List<OrderItemResponse> items;

}