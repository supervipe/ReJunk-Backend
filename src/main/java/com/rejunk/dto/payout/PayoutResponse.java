package com.rejunk.dto.payout;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class PayoutResponse {

    private UUID payoutId;

    private UUID orderItemId;

    private UUID sellerId;

    private BigDecimal saleAmount;

    private BigDecimal platformAmount;

    private BigDecimal sellerAmount;

    private String payoutStatus;

    private Instant createdAt;
}