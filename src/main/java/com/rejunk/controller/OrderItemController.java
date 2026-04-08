package com.rejunk.controller;

import com.rejunk.domain.model.OrderItem;
import com.rejunk.dto.orderItem.CreateOrderItemRequest;
import com.rejunk.dto.orderItem.OrderItemResponse;
import com.rejunk.service.OrderItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/order-items")
public class OrderItemController {

    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @PostMapping
    public OrderItem createOrderItem(@RequestBody CreateOrderItemRequest request) {
        return orderItemService.createOrderItem(request);
    }

    @GetMapping("/{id}")
    public OrderItemResponse getOrderItemById(@PathVariable UUID id) {
        return orderItemService.getOrderItemById(id);
    }

    @GetMapping("/order/{orderId}")
    public List<OrderItemResponse> getOrderItemsByOrder(@PathVariable UUID orderId) {
        return orderItemService.getOrderItemsByOrder(orderId);
    }
}