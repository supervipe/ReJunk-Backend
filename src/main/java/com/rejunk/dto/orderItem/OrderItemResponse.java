package com.rejunk.dto.orderItem;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class OrderItemResponse {

    private UUID orderItemId;

    private UUID orderId;

    private UUID listingId;

    private String itemTitle;

    private BigDecimal price;

    private String listingStatus;
}