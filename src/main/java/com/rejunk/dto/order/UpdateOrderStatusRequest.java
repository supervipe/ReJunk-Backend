package com.rejunk.dto.order;

import com.rejunk.domain.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderStatusRequest {

    private OrderStatus orderStatus;

}