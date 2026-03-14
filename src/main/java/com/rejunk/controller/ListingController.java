package com.rejunk.controller;

import com.rejunk.domain.model.Listing;
import com.rejunk.dto.listing.CreateListingRequest;
import com.rejunk.dto.listing.UpdateListingStatusRequest;
import com.rejunk.service.ListingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/listings")
public class ListingController {

    private final ListingService listingService;

    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    @PostMapping
    public Listing createListing(@RequestBody CreateListingRequest request) {
        return listingService.createListing(request);
    }

    @GetMapping
    public List<Listing> getActiveListings() {
        return listingService.getActiveListings();
    }

    @GetMapping("/all")
    public List<Listing> getAllListings() {
        return listingService.getAllListings();
    }

    @GetMapping("/{id}")
    public Listing getListingById(@PathVariable UUID id) {
        return listingService.getListingById(id);
    }

    @PatchMapping("/{id}/status")
    public Listing updateListingStatus(@PathVariable UUID id,
                                       @RequestBody UpdateListingStatusRequest request) {
        return listingService.updateListingStatus(id, request);
    }
}