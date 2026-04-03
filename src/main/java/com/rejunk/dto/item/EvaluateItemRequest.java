package com.rejunk.dto.item;

import com.rejunk.domain.enums.ItemCondition;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class EvaluateItemRequest {

    private ItemCondition itemCondition;
    private BigDecimal evaluatedPrice;
}