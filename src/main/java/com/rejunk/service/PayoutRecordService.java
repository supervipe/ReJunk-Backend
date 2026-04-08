package com.rejunk.service;

import com.rejunk.domain.enums.ListingStatus;
import com.rejunk.domain.enums.PayoutStatus;
import com.rejunk.domain.model.Listing;
import com.rejunk.domain.model.OrderItem;
import com.rejunk.domain.model.PayoutRecord;
import com.rejunk.domain.model.User;
import com.rejunk.dto.payout.CreatePayoutRequest;
import com.rejunk.dto.payout.PayoutResponse;
import com.rejunk.dto.payout.UpdatePayoutStatusRequest;
import com.rejunk.repository.ListingRepository;
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
    private final ListingRepository listingRepository;

    public PayoutRecordService(PayoutRecordRepository payoutRecordRepository,
                               OrderItemRepository orderItemRepository,
                               ListingRepository listingRepository) {
        this.payoutRecordRepository = payoutRecordRepository;
        this.orderItemRepository = orderItemRepository;
        this.listingRepository = listingRepository;
    }

    public PayoutResponse createPayout(CreatePayoutRequest dto) {
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
                .payoutStatus(PayoutStatus.PENDING)
                .build();

        return mapToResponse(payoutRecordRepository.save(payoutRecord));
    }

    public PayoutResponse getPayoutById(UUID id) {
        PayoutRecord payout = payoutRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payout record not found"));

        return mapToResponse(payout);
    }

    public List<PayoutResponse> getPayoutsBySeller(UUID sellerId) {
        return payoutRecordRepository.findBySellerId(sellerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public PayoutResponse updatePayoutStatus(UUID id, UpdatePayoutStatusRequest dto) {
        PayoutRecord payoutRecord = payoutRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payout record not found"));

        payoutRecord.setPayoutStatus(dto.getPayoutStatus());

        PayoutRecord updatedPayoutRecord = payoutRecordRepository.save(payoutRecord);
        if(updatedPayoutRecord.getPayoutStatus() == PayoutStatus.PROCESSED) {
            OrderItem orderItem = updatedPayoutRecord.getOrderItem();
            Listing listing = orderItem.getListing();
            listing.setListingStatus(ListingStatus.SOLD);
            listingRepository.save(listing);
        }
        return mapToResponse(updatedPayoutRecord);
    }

    private PayoutResponse mapToResponse(PayoutRecord payout) {
        return PayoutResponse.builder()
                .payoutId(payout.getId())
                .orderItemId(payout.getOrderItem().getId())
                .sellerId(payout.getSeller().getId())
                .saleAmount(payout.getSaleAmount())
                .platformAmount(payout.getPlatformAmount())
                .sellerAmount(payout.getSellerAmount())
                .payoutStatus(payout.getPayoutStatus().name())
                .createdAt(payout.getCreatedAt())
                .build();
    }
}