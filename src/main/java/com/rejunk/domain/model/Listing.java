package com.rejunk.domain.model;

import com.rejunk.domain.enums.ListingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
    name = "listing",
    uniqueConstraints = @UniqueConstraint(name = "uk_listing_item", columnNames = "item_id")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "listing_id", nullable = false, updatable = false)
    private UUID id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "listing_status", nullable = false, length = 20)
    private ListingStatus listingStatus = ListingStatus.ACTIVE;

}