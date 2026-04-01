package com.rejunk.service;

import com.rejunk.domain.enums.ListingStatus;
import com.rejunk.domain.enums.OrderStatus;
import com.rejunk.domain.model.*;
import com.rejunk.dto.order.CreateOrderRequest;
import com.rejunk.dto.order.OrderItemResponse;
import com.rejunk.dto.order.OrderResponse;
import com.rejunk.dto.order.UpdateOrderStatusRequest;
import com.rejunk.repository.ListingRepository;
import com.rejunk.repository.OrderRepository;
import com.rejunk.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import com.rejunk.domain.enums.ItemCondition;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ListingRepository listingRepository;

    @InjectMocks
    private OrderService orderService;

    private User buyer;
    private Listing listing;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        buyer = User.builder()
                .id(UUID.randomUUID())
                .fullName("Alice Buyer")
                .email("alice@example.com")
                .build();

        Item item = Item.builder()
                .id(UUID.randomUUID())
                .title("Toy Car")
                .condition(ItemCondition.GOOD)
                .build();

        listing = Listing.builder()
                .id(UUID.randomUUID())
                .item(item)
                .price(new BigDecimal("100.00"))
                .listingStatus(ListingStatus.ACTIVE)
                .build();
    }

    @Test
    void createOrder_shouldSaveOrderAndReturnResponse() {
        CreateOrderRequest request = CreateOrderRequest.builder()
                .buyerId(buyer.getId())
                .listingIds(List.of(listing.getId()))
                .build();

        when(userRepository.findById(buyer.getId())).thenReturn(Optional.of(buyer));
        when(listingRepository.findAllById(request.getListingIds())).thenReturn(List.of(listing));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> {
            Order o = i.getArgument(0);
            o.setId(UUID.randomUUID());
            o.setCreatedAt(Instant.now());
            return o;
        });

        OrderResponse response = orderService.createOrder(request);

        assertThat(response).isNotNull();
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getTotalAmount()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(listing.getListingStatus()).isEqualTo(ListingStatus.SOLD);

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrder_shouldThrowIfBuyerNotFound() {
        UUID unknownId = UUID.randomUUID();
        CreateOrderRequest request = CreateOrderRequest.builder()
                .buyerId(unknownId)
                .listingIds(List.of(listing.getId()))
                .build();

        when(userRepository.findById(unknownId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> orderService.createOrder(request));
        assertThat(ex.getMessage()).isEqualTo("Buyer not found");
    }

    @Test
    void createOrder_shouldThrowIfListingNotActive() {
        listing.setListingStatus(ListingStatus.SOLD);

        CreateOrderRequest request = CreateOrderRequest.builder()
                .buyerId(buyer.getId())
                .listingIds(List.of(listing.getId()))
                .build();

        when(userRepository.findById(buyer.getId())).thenReturn(Optional.of(buyer));
        when(listingRepository.findAllById(request.getListingIds())).thenReturn(List.of(listing));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> orderService.createOrder(request));
        assertThat(ex.getMessage()).isEqualTo("Listing not available");
    }

    @Test
    void getOrderById_shouldReturnOrder() {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setBuyer(buyer);
        order.setItems(List.of());
        order.setTotalAmount(BigDecimal.TEN);
        order.setOrderStatus(OrderStatus.PAID);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        OrderResponse response = orderService.getOrderById(order.getId());

        assertThat(response.getId()).isEqualTo(order.getId());
        assertThat(response.getTotalAmount()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    void getOrderById_shouldThrowIfNotFound() {
        UUID unknownId = UUID.randomUUID();
        when(orderRepository.findById(unknownId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> orderService.getOrderById(unknownId));
        assertThat(ex.getMessage()).isEqualTo("Order not found");
    }

    @Test
    void updateOrderStatus_shouldUpdateAndReturnResponse() {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setBuyer(buyer);
        order.setOrderStatus(OrderStatus.PAID);

        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setOrderStatus(OrderStatus.PAID);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        OrderResponse response = orderService.updateOrderStatus(order.getId(), request);

        assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void getOrdersByBuyer_shouldReturnResponses() {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setBuyer(buyer);
        order.setItems(List.of());
        order.setTotalAmount(BigDecimal.TEN);
        order.setOrderStatus(OrderStatus.PAID);

        when(orderRepository.findByBuyerId(buyer.getId())).thenReturn(List.of(order));

        List<OrderResponse> responses = orderService.getOrdersByBuyer(buyer.getId());

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(order.getId());
    }

    @Test
    void getAllOrders_shouldReturnResponses() {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setBuyer(buyer);
        order.setItems(List.of());
        order.setTotalAmount(BigDecimal.TEN);
        order.setOrderStatus(OrderStatus.PAID);

        when(orderRepository.findAll()).thenReturn(List.of(order));

        List<OrderResponse> responses = orderService.getAllOrders();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(order.getId());
    }
}