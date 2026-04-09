package com.rejunk.service;

import com.rejunk.domain.enums.ItemCondition;
import com.rejunk.domain.enums.NotificationType;
import com.rejunk.domain.model.CollectionRequest;
import com.rejunk.domain.model.Item;
import com.rejunk.domain.model.User;
import com.rejunk.dto.item.CreateItemRequest;
import com.rejunk.dto.item.EvaluateItemRequest;
import com.rejunk.repository.CollectionRequestRepository;
import com.rejunk.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CollectionRequestRepository collectionRequestRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ItemService itemService;

    private CollectionRequest request;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(UUID.randomUUID());

        request = new CollectionRequest();
        request.setId(UUID.randomUUID());
        request.setCustomer(user);
    }

    // CREATE ITEM

    @Test
    void createItem_shouldCreateAndSendNotification() {
        CreateItemRequest dto = new CreateItemRequest();
        dto.setCollectionRequestId(request.getId());
        dto.setTitle("Laptop");
        dto.setDescription("Good condition");
        dto.setCondition(ItemCondition.GOOD);

        when(collectionRequestRepository.findById(request.getId()))
                .thenReturn(Optional.of(request));

        when(itemRepository.save(any(Item.class)))
                .thenAnswer(i -> i.getArgument(0));

        Item result = itemService.createItem(dto);

        assertNotNull(result);
        assertEquals("Laptop", result.getTitle());
        assertEquals(ItemCondition.GOOD, result.getCondition());

        verify(notificationService).createNotification(
                eq(user.getId()),
                eq(NotificationType.ITEM_ADDED),
                contains("Laptop")
        );
    }

    @Test
    void createItem_shouldThrowIfRequestNotFound() {
        UUID id = UUID.randomUUID();

        CreateItemRequest dto = new CreateItemRequest();
        dto.setCollectionRequestId(id);

        when(collectionRequestRepository.findById(id))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> itemService.createItem(dto));

        assertEquals("Collection request not found", ex.getMessage());
    }

    // GET ITEMS BY REQUEST

    @Test
    void getItemsByCollectionRequest_shouldReturnList() {
        UUID requestId = request.getId();

        List<Item> items = List.of(new Item(), new Item());

        when(itemRepository.findByCollectionRequestId(requestId))
                .thenReturn(items);

        List<Item> result = itemService.getItemsByCollectionRequest(requestId);

        assertEquals(2, result.size());
    }


    // GET ITEM BY ID

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

    @Test
    void getItemById_shouldThrowIfNotFound() {
        UUID id = UUID.randomUUID();

        when(itemRepository.findById(id))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> itemService.getItemById(id));

        assertEquals("Item not found", ex.getMessage());
    }


    // EVALUATE ITEM

    @Test
    void evaluateItem_shouldUpdateAndNotify() {
        UUID itemId = UUID.randomUUID();

        Item item = new Item();
        item.setId(itemId);
        item.setTitle("Phone");
        item.setCollectionRequest(request);

        EvaluateItemRequest dto = new EvaluateItemRequest();
        dto.setItemCondition(ItemCondition.FAIR);
        dto.setEvaluatedPrice(new BigDecimal("50.00"));

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(item))
                .thenReturn(item);

        Item result = itemService.evaluateItem(itemId, dto);

        assertEquals(ItemCondition.FAIR, result.getCondition());
        assertEquals(new BigDecimal("50.00"), result.getEvaluatedPrice());

        verify(notificationService).createNotification(
                eq(user.getId()),
                eq(NotificationType.ITEM_PROCESSED), // matches your service
                contains("Phone")
        );
    }

    @Test
    void evaluateItem_shouldThrowIfNotFound() {
        UUID id = UUID.randomUUID();

        when(itemRepository.findById(id))
                .thenReturn(Optional.empty());

        EvaluateItemRequest dto = new EvaluateItemRequest();

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> itemService.evaluateItem(id, dto));

        assertEquals("Item not found", ex.getMessage());
    }
}