package com.rejunk.controller;

import com.rejunk.domain.model.Item;
import com.rejunk.dto.item.CreateItemRequest;
import com.rejunk.dto.item.EvaluateItemRequest;
import com.rejunk.service.ItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public Item createItem(@RequestBody CreateItemRequest request) {
        return itemService.createItem(request);
    }

    @GetMapping("/{id}")
    public Item getItemById(@PathVariable UUID id) {
        return itemService.getItemById(id);
    }

    @GetMapping("/collection-request/{collectionRequestId}")
    public List<Item> getItemsByCollectionRequest(@PathVariable UUID collectionRequestId) {
        return itemService.getItemsByCollectionRequest(collectionRequestId);
    }

    @PatchMapping("/{id}/evaluate")
    public Item evaluateItem(@PathVariable UUID id,
                             @RequestBody EvaluateItemRequest request) {
        return itemService.evaluateItem(id, request);
    }
}