package com.rejunk.dto.order;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateOrderRequest {

    private UUID buyerId;
    private List<UUID> listingIds;

}