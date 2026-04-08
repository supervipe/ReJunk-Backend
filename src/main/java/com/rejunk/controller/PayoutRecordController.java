package com.rejunk.controller;

import com.rejunk.dto.payout.CreatePayoutRequest;
import com.rejunk.dto.payout.PayoutResponse;
import com.rejunk.dto.payout.UpdatePayoutStatusRequest;
import com.rejunk.service.PayoutRecordService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payouts")
public class PayoutRecordController {

    private final PayoutRecordService payoutRecordService;

    public PayoutRecordController(PayoutRecordService payoutRecordService) {
        this.payoutRecordService = payoutRecordService;
    }

    @PostMapping
    public PayoutResponse createPayout(@RequestBody CreatePayoutRequest request) {
        return payoutRecordService.createPayout(request);
    }

    @GetMapping("/{id}")
    public PayoutResponse getPayoutById(@PathVariable UUID id) {
        return payoutRecordService.getPayoutById(id);
    }

    @GetMapping("/seller/{sellerId}")
    public List<PayoutResponse> getPayoutsBySeller(@PathVariable UUID sellerId) {
        return payoutRecordService.getPayoutsBySeller(sellerId);
    }

    @PatchMapping("/{id}/status")
    public PayoutResponse updatePayoutStatus(@PathVariable UUID id,
                                           @RequestBody UpdatePayoutStatusRequest request) {
        return payoutRecordService.updatePayoutStatus(id, request);
    }
}