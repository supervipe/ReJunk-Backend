package com.rejunk.controller;

import com.rejunk.dto.order.CreateOrderRequest;
import com.rejunk.dto.order.OrderResponse;
import com.rejunk.dto.order.UpdateOrderStatusRequest;
import com.rejunk.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public OrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable UUID id) {
        return orderService.getOrderById(id);
    }

    @GetMapping("/buyer/{buyerId}")
    public List<OrderResponse> getOrdersByBuyer(@PathVariable UUID buyerId) {
        return orderService.getOrdersByBuyer(buyerId);
    }

    @GetMapping
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PatchMapping("/{id}/status")
    public OrderResponse updateOrderStatus(@PathVariable UUID id,
                                           @RequestBody UpdateOrderStatusRequest request) {
        return orderService.updateOrderStatus(id, request);
    }
}