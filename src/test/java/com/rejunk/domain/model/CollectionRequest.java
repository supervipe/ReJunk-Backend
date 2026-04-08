package com.rejunk.domain.model;

import com.rejunk.domain.enums.PaymentStatus;
import com.rejunk.domain.enums.RequestStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CollectionRequestTest {

    @Test
    void testCollectionRequestProperties() {
        // Create a dummy User to assign as customer
        User customer = new User();
        customer.setId(UUID.randomUUID());
        customer.setFullName("John Doe");
        customer.setEmail("john@example.com");

        // Create CollectionRequest
        CollectionRequest request = new CollectionRequest();
        request.setId(UUID.randomUUID());
        request.setCustomer(customer);
        request.setPickupAddress("123 Main Street");
        request.setPreferredPickupTime(Instant.now());
        request.setPickupFee(new BigDecimal("15.00"));
        request.setPaymentStatus(PaymentStatus.PAID);
        request.setRequestStatus(RequestStatus.PAID);

        // Assertions
        assertNotNull(request.getId());
        assertNotNull(request.getCustomer());
        assertEquals("John Doe", request.getCustomer().getFullName());
        assertEquals("123 Main Street", request.getPickupAddress());
        assertNotNull(request.getPreferredPickupTime());
        assertEquals(new BigDecimal("15.00"), request.getPickupFee());
        assertEquals(PaymentStatus.PAID, request.getPaymentStatus());
        assertEquals(RequestStatus.PAID, request.getRequestStatus());
        assertNotNull(request.getItems());
        assertTrue(request.getItems().isEmpty());
    }
}