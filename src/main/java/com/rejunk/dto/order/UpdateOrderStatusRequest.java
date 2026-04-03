package com.rejunk.dto.order;

import com.rejunk.domain.enums.OrderStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class UpdateOrderStatusRequest {
    private String Status;


}
