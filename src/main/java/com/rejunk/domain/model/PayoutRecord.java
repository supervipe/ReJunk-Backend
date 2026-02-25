package com.rejunk.domain.model;

import com.rejunk.domain.enums.PayoutStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payout_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayoutRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payout_id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Column(name = "sale_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal saleAmount;

    @Column(name = "platform_commission_pct", nullable = false, precision = 5, scale = 2)
    private BigDecimal platformCommissionPct;

    @Column(name = "seller_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal sellerAmount;

    @Column(name = "platform_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal platformAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payout_status", nullable = false, length = 20)
    private PayoutStatus payoutStatus = PayoutStatus.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

}