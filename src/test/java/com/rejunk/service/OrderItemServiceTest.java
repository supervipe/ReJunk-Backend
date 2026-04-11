package com.rejunk.service;

import com.rejunk.domain.enums.ListingStatus;
import com.rejunk.domain.model.*;
import com.rejunk.repository.*;
import com.rejunk.dto.orderItem.CreateOrderItemRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderItemServiceTesting {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ListingRepository listingRepository;

    @Mock
    private PayoutRecordRepository payoutRecordRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private OrderItemService orderItemService;

    private Order order;
    private Listing listing;
    private Item item;
    private CollectionRequest request;
    private User seller;
    private User buyer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        seller = new User();
        seller.setId(UUID.randomUUID());

        buyer = new User();
        buyer.setId(UUID.randomUUID());

        request = new CollectionRequest();
        request.setCustomer(seller);

        item = new Item();
        item.setTitle("Laptop");
        item.setCollectionRequest(request);

        listing = new Listing();
        listing.setId(UUID.randomUUID());
        listing.setPrice(new BigDecimal("100.00"));
        listing.setListingStatus(ListingStatus.ACTIVE);
        listing.setItem(item);

        order = new Order();
        order.setId(UUID.randomUUID());
        order.setBuyer(buyer);
        order.setTotalAmount(BigDecimal.ZERO);
    }

    @Test
    void createOrderItem_shouldCreateSuccessfully() {
        CreateOrderItemRequest dto = new CreateOrderItemRequest();
        dto.setOrderId(order.getId());
        dto.setListingId(listing.getId());

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(listingRepository.findById(listing.getId())).thenReturn(Optional.of(listing));
        when(orderItemRepository.findByListingId(listing.getId())).thenReturn(Optional.empty());
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(i -> i.getArgument(0));
        when(payoutRecordRepository.findByOrderItemId(any())).thenReturn(Optional.empty());
        when(payoutRecordRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        OrderItem result = orderItemService.createOrderItem(dto);

        assertNotNull(result);
        assertEquals(listing.getPrice(), result.getUnitPrice());

        // listing should be SOLD
        assertEquals(ListingStatus.SOLD, listing.getListingStatus());

        // order total updated
        assertEquals(new BigDecimal("100.00"), order.getTotalAmount());

        verify(notificationRepository, times(3)).save(any(Notification.class));
    }

    @Test
    void createOrderItem_shouldThrowIfListingNotActive() {
        listing.setListingStatus(ListingStatus.SOLD);

        CreateOrderItemRequest dto = new CreateOrderItemRequest();
        dto.setOrderId(order.getId());
        dto.setListingId(listing.getId());

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(listingRepository.findById(listing.getId())).thenReturn(Optional.of(listing));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> orderItemService.createOrderItem(dto));

        assertEquals("Only active listings can be purchased", ex.getMessage());
    }

    @Test
    void createOrderItem_shouldThrowIfAlreadyExists() {
        CreateOrderItemRequest dto = new CreateOrderItemRequest();
        dto.setOrderId(order.getId());
        dto.setListingId(listing.getId());

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(listingRepository.findById(listing.getId())).thenReturn(Optional.of(listing));
        when(orderItemRepository.findByListingId(listing.getId()))
                .thenReturn(Optional.of(new OrderItem()));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> orderItemService.createOrderItem(dto));

        assertEquals("This listing is already attached to an order item", ex.getMessage());
    }

    @Test
    void getOrderItemById_shouldReturnItem() {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(UUID.randomUUID());
        orderItem.setOrder(order);
        orderItem.setListing(listing);
        orderItem.setUnitPrice(new BigDecimal("100.00"));

        when(orderItemRepository.findById(orderItem.getId()))
                .thenReturn(Optional.of(orderItem));

        var response = orderItemService.getOrderItemById(orderItem.getId());

        assertNotNull(response);
        assertEquals(orderItem.getId(), response.getOrderItemId());
    }

    @Test
    void getOrderItemById_shouldThrowIfNotFound() {
        UUID id = UUID.randomUUID();

        when(orderItemRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> orderItemService.getOrderItemById(id));

        assertEquals("Order item not found", ex.getMessage());
    }
}