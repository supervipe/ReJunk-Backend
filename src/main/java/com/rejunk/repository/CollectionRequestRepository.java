package com.rejunk.repository;

import com.rejunk.domain.model.CollectionRequest;
import com.rejunk.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CollectionRequestRepository extends JpaRepository<CollectionRequest, UUID> {

    List<CollectionRequest> findByCustomer(User customer);
}