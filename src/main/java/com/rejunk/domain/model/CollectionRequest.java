package com.rejunk.domain.model;

import com.rejunk.domain.enums.PaymentStatus;
import com.rejunk.domain.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "collection_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "request_id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @Column(name = "pickup_address", nullable = false, columnDefinition = "TEXT")
    private String pickupAddress;

    @Column(name = "preferred_pickup_time")
    private Instant preferredPickupTime;

    @Column(name = "pickup_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal pickupFee = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status", nullable = false, length = 20)
    private RequestStatus requestStatus = RequestStatus.SUBMITTED;

    @OneToMany(mappedBy = "collectionRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items = new ArrayList<>();


}