package com.rejunk.repository;

import com.rejunk.domain.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {

    List<Item> findByCollectionRequestId(UUID collectionRequestId);
}