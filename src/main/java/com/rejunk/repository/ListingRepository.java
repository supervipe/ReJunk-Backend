package com.rejunk.repository;

import com.rejunk.domain.enums.ListingStatus;
import com.rejunk.domain.model.Listing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ListingRepository extends JpaRepository<Listing, UUID> {

    List<Listing> findByListingStatus(ListingStatus listingStatus);

    Optional<Listing> findByItemId(UUID itemId);
}