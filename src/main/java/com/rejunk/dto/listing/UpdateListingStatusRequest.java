package com.rejunk.dto.listing;

import com.rejunk.domain.enums.ListingStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateListingStatusRequest {

    private ListingStatus listingStatus;
}