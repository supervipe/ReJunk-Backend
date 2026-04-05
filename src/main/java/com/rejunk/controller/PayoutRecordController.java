package com.rejunk.controller;

import com.rejunk.domain.model.PayoutRecord;
import com.rejunk.dto.payout.CreatePayoutRequest;
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
    public PayoutRecord createPayout(@RequestBody CreatePayoutRequest request) {
        return payoutRecordService.createPayout(request);
    }

    @GetMapping("/{id}")
    public PayoutRecord getPayoutById(@PathVariable UUID id) {
        return payoutRecordService.getPayoutById(id);
    }

    @GetMapping("/seller/{sellerId}")
    public List<PayoutRecord> getPayoutsBySeller(@PathVariable UUID sellerId) {
        return payoutRecordService.getPayoutsBySeller(sellerId);
    }

    @PatchMapping("/{id}/status")
    public PayoutRecord updatePayoutStatus(@PathVariable UUID id,
                                           @RequestBody UpdatePayoutStatusRequest request) {
        return payoutRecordService.updatePayoutStatus(id, request);
    }
}