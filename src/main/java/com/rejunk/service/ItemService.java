package com.rejunk.service;

import com.rejunk.domain.model.CollectionRequest;
import com.rejunk.domain.model.Item;
import com.rejunk.dto.item.CreateItemRequest;
import com.rejunk.dto.item.EvaluateItemRequest;
import com.rejunk.repository.CollectionRequestRepository;
import com.rejunk.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final CollectionRequestRepository collectionRequestRepository;

    public ItemService(ItemRepository itemRepository,
                       CollectionRequestRepository collectionRequestRepository) {
        this.itemRepository = itemRepository;
        this.collectionRequestRepository = collectionRequestRepository;
    }

    public Item createItem(CreateItemRequest dto) {
        CollectionRequest request = collectionRequestRepository
                .findById(dto.getCollectionRequestId())
                .orElseThrow(() -> new RuntimeException("Collection request not found"));

        Item item = Item.builder()
                .collectionRequest(request)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .condition(dto.getCondition())
                .build();

        return itemRepository.save(item);
    }

    public List<Item> getItemsByCollectionRequest(UUID collectionRequestId) {
        return itemRepository.findByCollectionRequestId(collectionRequestId);
    }

    public Item getItemById(UUID itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
    }

    public Item evaluateItem(UUID itemId, EvaluateItemRequest dto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        item.setCondition(dto.getItemCondition());
        item.setEvaluatedPrice(dto.getEvaluatedPrice());

        return itemRepository.save(item);
    }
}