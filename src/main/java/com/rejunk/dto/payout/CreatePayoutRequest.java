package com.rejunk.dto.payout;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CreatePayoutRequest {

    private UUID orderItemId;
    private BigDecimal platformCommissionPct;
}