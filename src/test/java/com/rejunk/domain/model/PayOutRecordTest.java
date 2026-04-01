package com.rejunk.domain.model;

import com.rejunk.domain.enums.PayoutStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PayoutRecordTest {

    @Test
    void testPayoutRecordProperties() {
        // Create objects for relations
        Order order = new Order();
        User seller = new User();

        // Create a PayoutRecord
        PayoutRecord payout = new PayoutRecord();
        UUID payoutId = UUID.randomUUID();
        payout.setId(payoutId);
        payout.setOrder(order);
        payout.setSeller(seller);
        payout.setSaleAmount(new BigDecimal("100.00"));
        payout.setPlatformCommissionPct(new BigDecimal("10.00"));
        payout.setSellerAmount(new BigDecimal("90.00"));
        payout.setPlatformAmount(new BigDecimal("10.00"));

        // Simulate @PrePersist
        payout.setCreatedAt(Instant.now());

        // Assertions
        assertEquals(payoutId, payout.getId());
        assertEquals(order, payout.getOrder());
        assertEquals(seller, payout.getSeller());
        assertEquals(new BigDecimal("100.00"), payout.getSaleAmount());
        assertEquals(new BigDecimal("10.00"), payout.getPlatformCommissionPct());
        assertEquals(new BigDecimal("90.00"), payout.getSellerAmount());
        assertEquals(new BigDecimal("10.00"), payout.getPlatformAmount());
        assertEquals(PayoutStatus.PENDING, payout.getPayoutStatus());
        assertNotNull(payout.getCreatedAt());
    }

    @Test
    void testDefaultPayoutStatus() {
        PayoutRecord payout = new PayoutRecord();
        assertEquals(PayoutStatus.PENDING, payout.getPayoutStatus());
    }

    @Test
    void testOnCreateSetsCreatedAt() {
        PayoutRecord payout = new PayoutRecord();
        payout.onCreate();
        assertNotNull(payout.getCreatedAt());
        assertTrue(payout.getCreatedAt().isBefore(Instant.now().plusSeconds(1)));
    }
}