package com.rejunk.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    @Test
    void testOrderItemProperties() {
        // Mock / create related objects
        Order order = new Order();
        order.setId(UUID.randomUUID());

        Listing listing = new Listing();
        listing.setId(UUID.randomUUID());

        // Create OrderItem
        OrderItem orderItem = new OrderItem();
        orderItem.setId(UUID.randomUUID());
        orderItem.setOrder(order);
        orderItem.setListing(listing);
        orderItem.setUnitPrice(new BigDecimal("25.50"));
        orderItem.setQuantity(3);

        // Assertions
        assertNotNull(orderItem.getId());
        assertEquals(order, orderItem.getOrder());
        assertEquals(listing, orderItem.getListing());
        assertEquals(new BigDecimal("25.50"), orderItem.getUnitPrice());
        assertEquals(3, orderItem.getQuantity());
    }

    @Test
    void testQuantitySetterGetter() {
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(5);
        assertEquals(5, orderItem.getQuantity());

        orderItem.setQuantity(10);
        assertEquals(10, orderItem.getQuantity());
    }
}