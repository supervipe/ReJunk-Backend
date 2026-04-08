package com.rejunk.dto.payout;

import com.rejunk.domain.enums.PayoutStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePayoutStatusRequest {

    private PayoutStatus payoutStatus;
}
