package com.rejunk.dto.collection;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class CreateCollectionRequest {

    private UUID customerId;

    private String pickupAddress;

    private Instant preferredPickupTime;

    private BigDecimal pickupFee;
}