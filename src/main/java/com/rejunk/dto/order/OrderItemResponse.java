package com.rejunk.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class OrderItemResponse {

    private UUID id;
    private UUID listingId;
    private String title;
    private String condition;
    private BigDecimal unitPrice;

}