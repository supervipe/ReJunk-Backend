package com.rejunk.dto.item;

import com.rejunk.domain.enums.ItemCondition;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateItemRequest {

    private UUID collectionRequestId;

    private String title;

    private String description;

    private ItemCondition condition;
}