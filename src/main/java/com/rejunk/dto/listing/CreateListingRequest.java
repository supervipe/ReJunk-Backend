package com.rejunk.dto.listing;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor

public class CreateListingRequest {

    private UUID itemId;
    private BigDecimal price;
}