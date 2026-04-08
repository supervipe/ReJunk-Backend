package com.rejunk.service;

import com.rejunk.domain.enums.ItemCondition;
import com.rejunk.domain.enums.ListingStatus;
import com.rejunk.domain.model.Item;
import com.rejunk.domain.model.Listing;
import com.rejunk.dto.listing.CreateListingRequest;
import com.rejunk.dto.listing.UpdateListingStatusRequest;
import com.rejunk.repository.ItemRepository;
import com.rejunk.repository.ListingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ListingServiceTest {

    private ItemRepository itemRepository;
    private ListingRepository listingRepository;
    private ListingService listingService;

    @BeforeEach
    void setUp() {
        itemRepository = Mockito.mock(ItemRepository.class);
        listingRepository = Mockito.mock(ListingRepository.class);
        listingService = new ListingService(listingRepository, itemRepository);
    }

    @Test
    void createListing_shouldCreateSuccessfully() {
        UUID itemId = UUID.randomUUID();
        CreateListingRequest request = new CreateListingRequest(itemId, BigDecimal.valueOf(100.0));

        Item item = Item.builder()
                .id(itemId)
                .title("Toy Car")
                .condition(ItemCondition.GOOD)
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(listingRepository.findByItemId(itemId)).thenReturn(Optional.empty());
        when(listingRepository.save(any(Listing.class))).thenAnswer(i -> i.getArguments()[0]);

        Listing listing = listingService.createListing(request);

        assertNotNull(listing);
        assertEquals(item, listing.getItem());
        assertEquals(ListingStatus.ACTIVE, listing.getListingStatus());
        assertEquals(BigDecimal.valueOf(100.0), listing.getPrice());

        verify(itemRepository, times(1)).findById(itemId);
        verify(listingRepository, times(1)).save(listing);
    }

    @Test
    void createListing_shouldThrowIfItemNotFound() {
        UUID itemId = UUID.randomUUID();
        CreateListingRequest request = new CreateListingRequest(itemId, BigDecimal.valueOf(100.0));

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            listingService.createListing(request);
        });

        assertEquals("Item not found", exception.getMessage());
    }

    @Test
    void createListing_shouldThrowIfListingAlreadyExists() {
        UUID itemId = UUID.randomUUID();
        CreateListingRequest request = new CreateListingRequest(itemId, BigDecimal.valueOf(100.0));

        Item item = Item.builder()
                .id(itemId)
                .title("Toy Car")
                .condition(ItemCondition.GOOD)
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(listingRepository.findByItemId(itemId)).thenReturn(Optional.of(new Listing()));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            listingService.createListing(request);
        });

        assertEquals("Listing already exists for this item", exception.getMessage());
    }

    @Test
    void updateListingStatus_shouldUpdateSuccessfully() {
        UUID listingId = UUID.randomUUID();
        UpdateListingStatusRequest request = new UpdateListingStatusRequest(ListingStatus.SOLD);

        Listing listing = Listing.builder()
                .id(listingId)
                .listingStatus(ListingStatus.ACTIVE)
                .build();

        when(listingRepository.findById(listingId)).thenReturn(Optional.of(listing));
        when(listingRepository.save(any(Listing.class))).thenAnswer(i -> i.getArguments()[0]);

        Listing updated = listingService.updateListingStatus(listingId, request);

        assertEquals(ListingStatus.SOLD, updated.getListingStatus());
        verify(listingRepository, times(1)).save(listing);
    }
}