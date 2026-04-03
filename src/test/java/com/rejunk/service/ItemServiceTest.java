package com.rejunk.service;

import com.rejunk.domain.enums.ItemCondition;
import com.rejunk.domain.model.CollectionRequest;
import com.rejunk.domain.model.Item;
import com.rejunk.dto.item.CreateItemRequest;
import com.rejunk.dto.item.EvaluateItemRequest;
import com.rejunk.repository.CollectionRequestRepository;
import com.rejunk.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CollectionRequestRepository collectionRequestRepository;

    @InjectMocks
    private ItemService itemService;

    //  Test createItem
    @Test
    void createItem_shouldCreateItemSuccessfully() {
        UUID requestId = UUID.randomUUID();

        CollectionRequest request = new CollectionRequest();
        request.setId(requestId);

        CreateItemRequest dto = CreateItemRequest.builder()
                .collectionRequestId(requestId)
                .title("Toy Car")
                .description("Kids toy")
                .condition(ItemCondition.GOOD)
                .build();

        when(collectionRequestRepository.findById(requestId))
                .thenReturn(Optional.of(request));

        when(itemRepository.save(any(Item.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Item result = itemService.createItem(dto);

        assertNotNull(result);
        assertEquals("Toy Car", result.getTitle());
        assertEquals(ItemCondition.GOOD, result.getCondition());
        assertEquals(request, result.getCollectionRequest());

        verify(itemRepository).save(any(Item.class));
    }

    // Test createItem when collection request not found
    @Test
    void createItem_shouldThrowIfCollectionRequestNotFound() {
        UUID requestId = UUID.randomUUID();

        CreateItemRequest dto = CreateItemRequest.builder()
                .collectionRequestId(requestId)
                .title("Toy Car")
                .build();

        when(collectionRequestRepository.findById(requestId))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> itemService.createItem(dto));

        assertEquals("Collection request not found", ex.getMessage());
    }

    //  Test getItemsByCollectionRequest
    @Test
    void getItemsByCollectionRequest_shouldReturnItems() {
        UUID requestId = UUID.randomUUID();

        List<Item> items = List.of(new Item(), new Item());

        when(itemRepository.findByCollectionRequestId(requestId))
                .thenReturn(items);

        List<Item> result = itemService.getItemsByCollectionRequest(requestId);

        assertEquals(2, result.size());
        verify(itemRepository).findByCollectionRequestId(requestId);
    }

    //  Test getItemById
    @Test
    void getItemById_shouldReturnItem() {
        UUID itemId = UUID.randomUUID();

        Item item = new Item();
        item.setId(itemId);

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        Item result = itemService.getItemById(itemId);

        assertEquals(itemId, result.getId());
    }

    //  Test getItemById not found
    @Test
    void getItemById_shouldThrowIfNotFound() {
        UUID itemId = UUID.randomUUID();

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> itemService.getItemById(itemId));

        assertEquals("Item not found", ex.getMessage());
    }

    //  Test evaluateItem
    @Test
    void evaluateItem_shouldUpdateItem() {
        UUID itemId = UUID.randomUUID();

        Item item = new Item();
        item.setId(itemId);

        EvaluateItemRequest dto = EvaluateItemRequest.builder()
                .itemCondition(ItemCondition.FAIR)
                .evaluatedPrice(new BigDecimal("25.00"))
                .build();

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        when(itemRepository.save(any(Item.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Item result = itemService.evaluateItem(itemId, dto);

        assertEquals(ItemCondition.FAIR, result.getCondition());
        assertEquals(new BigDecimal("25.00"), result.getEvaluatedPrice());

        verify(itemRepository).save(item);
    }

    //  Test evaluateItem not found
    @Test
    void evaluateItem_shouldThrowIfItemNotFound() {
        UUID itemId = UUID.randomUUID();

        EvaluateItemRequest dto =  EvaluateItemRequest.builder()
                .itemCondition(ItemCondition.GOOD)
                .evaluatedPrice(new BigDecimal("25.00"))
                .build();;

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> itemService.evaluateItem(itemId, dto));

        assertEquals("Item not found", ex.getMessage());
    }
}