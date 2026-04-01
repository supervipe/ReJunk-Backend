package com.rejunk.domain.model;

import com.rejunk.domain.enums.ListingStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ListingTest {

    @Test
    void testListingProperties() {
        // Create a dummy Item to assign to the Listing
        Item item = new Item();
        item.setId(UUID.randomUUID());
        item.setTitle("Toy Car");

        // Build Listing
        Listing listing = new Listing();
        listing.setId(UUID.randomUUID());
        listing.setItem(item);
        listing.setPrice(new BigDecimal("25.50"));
        listing.setListingStatus(ListingStatus.ACTIVE);

        // Assertions
        assertNotNull(listing.getId());
        assertNotNull(listing.getItem());
        assertEquals("Toy Car", listing.getItem().getTitle());
        assertEquals(new BigDecimal("25.50"), listing.getPrice());
        assertEquals(ListingStatus.ACTIVE, listing.getListingStatus());
    }
}