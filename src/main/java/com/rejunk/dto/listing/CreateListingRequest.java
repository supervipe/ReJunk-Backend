package com.rejunk.dto.listing;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class CreateListingRequest {

    private UUID itemId;
    private BigDecimal price;
}