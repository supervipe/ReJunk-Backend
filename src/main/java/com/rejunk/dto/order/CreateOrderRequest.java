package com.rejunk.dto.order;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Data
@Builder
public class CreateOrderRequest {

    private UUID buyerId;
    private List<UUID> listingIds;

}