package com.rejunk.service;

import com.rejunk.domain.enums.ListingStatus;
import com.rejunk.domain.enums.NotificationType;
import com.rejunk.domain.enums.OrderStatus;
import com.rejunk.domain.model.Listing;
import com.rejunk.domain.model.Order;
import com.rejunk.domain.model.OrderItem;
import com.rejunk.domain.model.User;
import com.rejunk.dto.order.CreateOrderRequest;
import com.rejunk.dto.order.OrderItemResponse;
import com.rejunk.dto.order.OrderResponse;
import com.rejunk.dto.order.UpdateOrderStatusRequest;
import com.rejunk.repository.ListingRepository;
import com.rejunk.repository.OrderRepository;
import com.rejunk.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ListingRepository listingRepository;
    private final NotificationService notificationService;

    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        ListingRepository listingRepository,
                        NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.listingRepository = listingRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest dto) {

        User buyer = userRepository.findById(dto.getBuyerId())
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        List<Listing> listings = listingRepository.findAllById(dto.getListingIds());

        Order order = new Order();
        order.setBuyer(buyer);
        order.setOrderStatus(OrderStatus.PAID);

        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Listing listing : listings) {

            if (listing.getListingStatus() != ListingStatus.ACTIVE) {
                throw new RuntimeException("Listing not available");
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setListing(listing);
            item.setUnitPrice(listing.getPrice());

            items.add(item);
            total = total.add(listing.getPrice());

            listing.setListingStatus(ListingStatus.SOLD);

            UUID sellerId = listing.getItem()
                    .getCollectionRequest()
                    .getCustomer()
                    .getId();

            String message = "Your item '" + listing.getItem().getTitle() + "' has been sold.";

            notificationService.createNotification(
                    sellerId,
                    NotificationType.ITEM_SOLD,
                    message
            );
        }

        order.setItems(items);
        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);

        return toResponse(saved);
    }

    public OrderResponse getOrderById(UUID id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return toResponse(order);
    }

    public List<OrderResponse> getOrdersByBuyer(UUID buyerId) {

        List<Order> orders = orderRepository.findByBuyerId(buyerId);

        List<OrderResponse> responses = new ArrayList<>();

        for (Order order : orders) {
            responses.add(toResponse(order));
        }

        return responses;
    }

    public List<OrderResponse> getAllOrders() {

        List<OrderResponse> responses = new ArrayList<>();

        for (Order order : orderRepository.findAll()) {
            responses.add(toResponse(order));
        }

        return responses;
    }

    @Transactional
    public OrderResponse updateOrderStatus(UUID id, UpdateOrderStatusRequest dto) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setOrderStatus(dto.getOrderStatus());

        return toResponse(orderRepository.save(order));
    }

    private OrderResponse toResponse(Order order) {

        List<OrderItemResponse> itemResponses = new ArrayList<>();

        for (OrderItem item : order.getItems()) {

            itemResponses.add(OrderItemResponse.builder()
                    .id(item.getId())
                    .listingId(item.getListing().getId())
                    .title(item.getListing().getItem().getTitle())
                    .condition(item.getListing().getItem().getCondition().name())
                    .unitPrice(item.getUnitPrice())
                    .build());
        }

        return OrderResponse.builder()
                .id(order.getId())
                .totalAmount(order.getTotalAmount())
                .orderStatus(order.getOrderStatus())
                .createdAt(order.getCreatedAt())
                .items(itemResponses)
                .build();
    }
}


//package com.rejunk.service;
//
//import com.rejunk.domain.enums.ListingStatus;
//import com.rejunk.domain.enums.OrderStatus;
//import com.rejunk.domain.model.Listing;
//import com.rejunk.domain.model.Order;
//import com.rejunk.domain.model.OrderItem;
//import com.rejunk.domain.model.User;
//import com.rejunk.dto.order.CreateOrderRequest;
//import com.rejunk.dto.order.OrderItemResponse;
//import com.rejunk.dto.order.OrderResponse;
//import com.rejunk.dto.order.UpdateOrderStatusRequest;
//import com.rejunk.repository.ListingRepository;
//import com.rejunk.repository.OrderRepository;
//import com.rejunk.repository.UserRepository;
//import jakarta.transaction.Transactional;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.util.*;
//
//@Service
//public class OrderService {
//
//    private final OrderRepository orderRepository;
//    private final UserRepository userRepository;
//    private final ListingRepository listingRepository;
//
//    public OrderService(OrderRepository orderRepository,
//                        UserRepository userRepository,
//                        ListingRepository listingRepository) {
//        this.orderRepository = orderRepository;
//        this.userRepository = userRepository;
//        this.listingRepository = listingRepository;
//    }
//
//    @Transactional
//    public OrderResponse createOrder(CreateOrderRequest dto) {
//
//        User buyer = userRepository.findById(dto.getBuyerId())
//                .orElseThrow(() -> new RuntimeException("Buyer not found"));
//
//        List<Listing> listings = listingRepository.findAllById(dto.getListingIds());
//
//        Order order = new Order();
//        order.setBuyer(buyer);
//        order.setOrderStatus(OrderStatus.PAID);
//
//        List<OrderItem> items = new ArrayList<>();
//        BigDecimal total = BigDecimal.ZERO;
//
//        for (Listing listing : listings) {
//
//            if (listing.getListingStatus() != ListingStatus.ACTIVE) {
//                throw new RuntimeException("Listing not available");
//            }
//
//            OrderItem item = new OrderItem();
//            item.setOrder(order);
//            item.setListing(listing);
//            item.setUnitPrice(listing.getPrice());
//
//            items.add(item);
//
//            total = total.add(listing.getPrice());
//
//            listing.setListingStatus(ListingStatus.SOLD);
//        }
//
//        order.setItems(items);
//        order.setTotalAmount(total);
//
//        Order saved = orderRepository.save(order);
//
//        return toResponse(saved);
//    }
//
//    public OrderResponse getOrderById(UUID id) {
//
//        Order order = orderRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Order not found"));
//
//        return toResponse(order);
//    }
//
//    public List<OrderResponse> getOrdersByBuyer(UUID buyerId) {
//
//        List<Order> orders = orderRepository.findByBuyerId(buyerId);
//
//        List<OrderResponse> responses = new ArrayList<>();
//
//        for (Order order : orders) {
//            responses.add(toResponse(order));
//        }
//
//        return responses;
//    }
//
//    public List<OrderResponse> getAllOrders() {
//
//        List<OrderResponse> responses = new ArrayList<>();
//
//        for (Order order : orderRepository.findAll()) {
//            responses.add(toResponse(order));
//        }
//
//        return responses;
//    }
//
//    @Transactional
//    public OrderResponse updateOrderStatus(UUID id, UpdateOrderStatusRequest dto) {
//
//        Order order = orderRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Order not found"));
//
//        order.setOrderStatus(dto.getOrderStatus());
//
//        return toResponse(orderRepository.save(order));
//    }
//
//    private OrderResponse toResponse(Order order) {
//
//        List<OrderItemResponse> itemResponses = new ArrayList<>();
//
//        for (OrderItem item : order.getItems()) {
//
//            itemResponses.add(OrderItemResponse.builder()
//                    .id(item.getId())
//                    .listingId(item.getListing().getId())
//                    .title(item.getListing().getItem().getTitle())
//                    .condition(item.getListing().getItem().getCondition().name())
//                    .unitPrice(item.getUnitPrice())
//                    .build());
//        }
//
//        return OrderResponse.builder()
//                .id(order.getId())
//                .totalAmount(order.getTotalAmount())
//                .orderStatus(order.getOrderStatus())
//                .createdAt(order.getCreatedAt())
//                .items(itemResponses)
//                .build();
//    }
//}