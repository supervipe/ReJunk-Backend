package com.rejunk.domain.model;

import com.rejunk.domain.enums.EvaluationStatus;
import com.rejunk.domain.enums.ItemCondition;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "item_id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private CollectionRequest collectionRequest;

    @Column(name = "title", nullable = false, length = 120)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition", nullable = false, length = 20)
    private ItemCondition condition;

    @Enumerated(EnumType.STRING)
    @Column(name = "evaluation_status", nullable = false, length = 20)
    private EvaluationStatus evaluationStatus = EvaluationStatus.PENDING;

    @Column(name = "evaluated_price", precision = 10, scale = 2)
    private BigDecimal evaluatedPrice;

    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private Listing listing;

}