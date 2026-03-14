package com.rejunk.service;

import com.rejunk.domain.enums.ListingStatus;
import com.rejunk.domain.model.Item;
import com.rejunk.domain.model.Listing;
import com.rejunk.dto.listing.CreateListingRequest;
import com.rejunk.dto.listing.UpdateListingStatusRequest;
import com.rejunk.repository.ItemRepository;
import com.rejunk.repository.ListingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ListingService {

    private final ListingRepository listingRepository;
    private final ItemRepository itemRepository;

    public ListingService(ListingRepository listingRepository, ItemRepository itemRepository) {
        this.listingRepository = listingRepository;
        this.itemRepository = itemRepository;
    }

    public Listing createListing(CreateListingRequest dto) {

        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (listingRepository.findByItemId(dto.getItemId()).isPresent()) {
            throw new RuntimeException("Listing already exists for this item");
        }

        Listing listing = Listing.builder()
                .item(item)
                .price(dto.getPrice())
                .listingStatus(ListingStatus.ACTIVE)
                .build();

        return listingRepository.save(listing);
    }

    public List<Listing> getActiveListings() {
        return listingRepository.findByListingStatus(ListingStatus.ACTIVE);
    }

    public List<Listing> getAllListings() {
        return listingRepository.findAll();
    }

    public Listing getListingById(UUID id) {
        return listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
    }

    public Listing updateListingStatus(UUID id, UpdateListingStatusRequest dto) {

        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        listing.setListingStatus(dto.getListingStatus());

        return listingRepository.save(listing);
    }
}