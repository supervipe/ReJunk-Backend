package com.rejunk.repository;

import com.rejunk.domain.model.PayoutRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PayoutRecordRepository extends JpaRepository<PayoutRecord, UUID> {

    List<PayoutRecord> findBySellerId(UUID sellerId);

    Optional<PayoutRecord> findByOrderItemId(UUID orderItemId);
}
