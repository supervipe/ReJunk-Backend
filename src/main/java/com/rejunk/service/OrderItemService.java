package com.rejunk.service;

import com.rejunk.domain.enums.ListingStatus;
import com.rejunk.domain.enums.NotificationType;
import com.rejunk.domain.model.Listing;
import com.rejunk.domain.model.Notification;
import com.rejunk.domain.model.Order;
import com.rejunk.domain.model.OrderItem;
import com.rejunk.domain.model.PayoutRecord;
import com.rejunk.domain.model.User;
import com.rejunk.dto.orderItem.CreateOrderItemRequest;
import com.rejunk.dto.orderItem.OrderItemResponse;
import com.rejunk.repository.ListingRepository;
import com.rejunk.repository.NotificationRepository;
import com.rejunk.repository.OrderItemRepository;
import com.rejunk.repository.OrderRepository;
import com.rejunk.repository.PayoutRecordRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ListingRepository listingRepository;
    private final PayoutRecordRepository payoutRecordRepository;
    private final NotificationRepository notificationRepository;

    public OrderItemService(OrderItemRepository orderItemRepository,
                            OrderRepository orderRepository,
                            ListingRepository listingRepository,
                            PayoutRecordRepository payoutRecordRepository,
                            NotificationRepository notificationRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.listingRepository = listingRepository;
        this.payoutRecordRepository = payoutRecordRepository;
        this.notificationRepository = notificationRepository;
    }

    public OrderItem createOrderItem(CreateOrderItemRequest dto) {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Listing listing = listingRepository.findById(dto.getListingId())
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        if (listing.getListingStatus() != ListingStatus.ACTIVE) {
            throw new RuntimeException("Only active listings can be purchased");
        }

        if (orderItemRepository.findByListingId(dto.getListingId()).isPresent()) {
            throw new RuntimeException("This listing is already attached to an order item");
        }

        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .listing(listing)
                .unitPrice(listing.getPrice())
                .build();

        OrderItem savedOrderItem = orderItemRepository.save(orderItem);

        listing.setListingStatus(ListingStatus.SOLD);
        listingRepository.save(listing);

        BigDecimal currentTotal = order.getTotalAmount() == null ? BigDecimal.ZERO : order.getTotalAmount();
        order.setTotalAmount(currentTotal.add(listing.getPrice()));
        orderRepository.save(order);

        PayoutRecord payoutRecord = createAutomaticPayout(savedOrderItem);

        createSellerSaleNotification(savedOrderItem);
        createBuyerPurchaseNotification(savedOrderItem);
        createPayoutNotification(payoutRecord);

        return savedOrderItem;
    }

    private PayoutRecord createAutomaticPayout(OrderItem orderItem) {
        return payoutRecordRepository.findByOrderItemId(orderItem.getId())
                .orElseGet(() -> {
                    User seller = orderItem.getListing()
                            .getItem()
                            .getCollectionRequest()
                            .getCustomer();

                    BigDecimal saleAmount = orderItem.getUnitPrice();
                    BigDecimal commissionPct = new BigDecimal("50.00");

                    BigDecimal platformAmount = saleAmount.multiply(commissionPct)
                            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

                    BigDecimal sellerAmount = saleAmount.subtract(platformAmount);

                    PayoutRecord payoutRecord = PayoutRecord.builder()
                            .orderItem(orderItem)
                            .seller(seller)
                            .saleAmount(saleAmount)
                            .platformCommissionPct(commissionPct)
                            .sellerAmount(sellerAmount)
                            .platformAmount(platformAmount)
                            .build();

                    return payoutRecordRepository.save(payoutRecord);
                });
    }

    private void createSellerSaleNotification(OrderItem orderItem) {
        User seller = orderItem.getListing()
                .getItem()
                .getCollectionRequest()
                .getCustomer();

        String itemTitle = orderItem.getListing().getItem().getTitle();

        Notification notification = Notification.builder()
                .user(seller)
                .type(NotificationType.ITEM_SOLD)
                .message("Your item \"" + itemTitle + "\" has been sold successfully.")
                .read(false)
                .build();

        notificationRepository.save(notification);
    }

    private void createBuyerPurchaseNotification(OrderItem orderItem) {
        User buyer = orderItem.getOrder().getBuyer();
        String itemTitle = orderItem.getListing().getItem().getTitle();

        Notification notification = Notification.builder()
                .user(buyer)
                .type(NotificationType.ITEM_PROCESSED)
                .message("Your purchase of \"" + itemTitle + "\" was completed successfully.")
                .read(false)
                .build();

        notificationRepository.save(notification);
    }

    private void createPayoutNotification(PayoutRecord payoutRecord) {
        User seller = payoutRecord.getSeller();

        Notification notification = Notification.builder()
                .user(seller)
                .type(NotificationType.PAYOUT_CREATED)
                .message("A payout of $" + payoutRecord.getSellerAmount() + " has been created for your sold item.")
                .read(false)
                .build();

        notificationRepository.save(notification);
    }

    public List<OrderItemResponse> getOrderItemsByOrder(UUID orderId) {
        return orderItemRepository.findByOrderId(orderId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public OrderItemResponse getOrderItemById(UUID id) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order item not found"));
        return mapToResponse(orderItem);
    }

    private OrderItemResponse mapToResponse(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .orderItemId(orderItem.getId())
                .orderId(orderItem.getOrder().getId())
                .listingId(orderItem.getListing().getId())
                .itemTitle(orderItem.getListing().getItem().getTitle())
                .price(orderItem.getUnitPrice())
                .listingStatus(orderItem.getListing().getListingStatus().name())
                .build();
    }
}