package com.rejunk.service;

import com.rejunk.domain.model.OrderItem;
import com.rejunk.domain.model.PayoutRecord;
import com.rejunk.domain.model.User;
import com.rejunk.dto.payout.CreatePayoutRequest;
import com.rejunk.dto.payout.UpdatePayoutStatusRequest;
import com.rejunk.repository.OrderItemRepository;
import com.rejunk.repository.PayoutRecordRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class PayoutRecordService {

    private final PayoutRecordRepository payoutRecordRepository;
    private final OrderItemRepository orderItemRepository;

    public PayoutRecordService(PayoutRecordRepository payoutRecordRepository,
                               OrderItemRepository orderItemRepository) {
        this.payoutRecordRepository = payoutRecordRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public PayoutRecord createPayout(CreatePayoutRequest dto) {
        OrderItem orderItem = orderItemRepository.findById(dto.getOrderItemId())
                .orElseThrow(() -> new RuntimeException("Order item not found"));

        if (payoutRecordRepository.findByOrderItemId(dto.getOrderItemId()).isPresent()) {
            throw new RuntimeException("Payout already exists for this order item");
        }

        User seller = orderItem.getListing()
                .getItem()
                .getCollectionRequest()
                .getCustomer();

        BigDecimal saleAmount = orderItem.getUnitPrice();
        BigDecimal commissionPct = dto.getPlatformCommissionPct();
        BigDecimal platformAmount = saleAmount.multiply(commissionPct)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal sellerAmount = saleAmount.subtract(platformAmount);

        PayoutRecord payoutRecord = PayoutRecord.builder()
                .orderItem(orderItem)
                .seller(seller)
                .saleAmount(saleAmount)
                .platformCommissionPct(commissionPct)
                .sellerAmount(sellerAmount)
                .platformAmount(platformAmount)
                .build();

        return payoutRecordRepository.save(payoutRecord);
    }

    public List<PayoutRecord> getPayoutsBySeller(UUID sellerId) {
        return payoutRecordRepository.findBySellerId(sellerId);
    }

    public PayoutRecord getPayoutById(UUID id) {
        return payoutRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payout record not found"));
    }

    public PayoutRecord updatePayoutStatus(UUID id, UpdatePayoutStatusRequest dto) {
        PayoutRecord payoutRecord = payoutRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payout record not found"));

        payoutRecord.setPayoutStatus(dto.getPayoutStatus());

        return payoutRecordRepository.save(payoutRecord);
    }
}