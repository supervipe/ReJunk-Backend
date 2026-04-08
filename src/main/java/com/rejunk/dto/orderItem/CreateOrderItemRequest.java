package com.rejunk.dto.orderItem;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateOrderItemRequest {

    private UUID orderId;
    private UUID listingId;
}
