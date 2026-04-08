package com.rejunk.dto.listing;

import com.rejunk.domain.enums.ListingStatus;
import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor

public class UpdateListingStatusRequest {

    private ListingStatus listingStatus;
}