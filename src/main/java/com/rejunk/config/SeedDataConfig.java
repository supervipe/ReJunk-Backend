package com.rejunk.config;

import com.rejunk.domain.enums.*;
import com.rejunk.domain.model.*;
import com.rejunk.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Configuration
public class SeedDataConfig {

    @Bean
    CommandLineRunner seedAll(
            UserRepository userRepository,
            CollectionRequestRepository collectionRequestRepository,
            ItemRepository itemRepository,
            ListingRepository listingRepository,
            NotificationRepository notificationRepository,
            OrderRepository orderRepository,
            PasswordEncoder encoder
    ) {
        return args -> {

            if (userRepository.count() > 2) return;

            // USERS
            User admin = User.builder()
                    .fullName("Admin User")
                    .email("admin@rejunk.com")
                    .phone("0000000000")
                    .passwordHash(encoder.encode("admin123"))
                    .role(UserRole.ADMIN)
                    .status(AccountStatus.ACTIVE)
                    .build();

            User customer1 = User.builder()
                    .fullName("John Customer")
                    .email("customer1@rejunk.com")
                    .phone("1111111111")
                    .passwordHash(encoder.encode("customer123"))
                    .role(UserRole.CUSTOMER)
                    .status(AccountStatus.ACTIVE)
                    .build();

            User customer2 = User.builder()
                    .fullName("Jane Customer")
                    .email("customer2@rejunk.com")
                    .phone("2222222222")
                    .passwordHash(encoder.encode("customer123"))
                    .role(UserRole.CUSTOMER)
                    .status(AccountStatus.ACTIVE)
                    .build();

            userRepository.saveAll(List.of(admin, customer1, customer2));

            // COLLECTION REQUESTS
            CollectionRequest request1 = CollectionRequest.builder()
                    .customer(customer1)
                    .pickupAddress("123 Main Street")
                    .preferredPickupTime(Instant.now())
                    .pickupFee(new BigDecimal("25.00"))
                    .paymentStatus(PaymentStatus.PAID)
                    .requestStatus(RequestStatus.SUBMITTED)
                    .build();

            CollectionRequest request2 = CollectionRequest.builder()
                    .customer(customer2)
                    .pickupAddress("456 Oak Avenue")
                    .preferredPickupTime(Instant.now())
                    .pickupFee(new BigDecimal("30.00"))
                    .paymentStatus(PaymentStatus.UNPAID)
                    .requestStatus(RequestStatus.SUBMITTED)
                    .build();

            collectionRequestRepository.saveAll(List.of(request1, request2));

            // ITEMS
            Item item1 = Item.builder()
                    .collectionRequest(request1)
                    .title("Wooden Table")
                    .description("Dining table")
                    .condition(ItemCondition.GOOD)
                    .build();

            Item item2 = Item.builder()
                    .collectionRequest(request1)
                    .title("Office Chair")
                    .description("Comfortable chair")
                    .condition(ItemCondition.FAIR)
                    .build();

            Item item3 = Item.builder()
                    .collectionRequest(request2)
                    .title("Bookshelf")
                    .description("Large bookshelf")
                    .condition(ItemCondition.GOOD)
                    .build();

            itemRepository.saveAll(List.of(item1, item2, item3));

            // LISTINGS
            Listing listing1 = Listing.builder()
                    .item(item1)
                    .price(new BigDecimal("80.00"))
                    .listingStatus(ListingStatus.ACTIVE)
                    .build();

            Listing listing2 = Listing.builder()
                    .item(item2)
                    .price(new BigDecimal("40.00"))
                    .listingStatus(ListingStatus.SOLD)
                    .build();

            Listing listing3 = Listing.builder()
                    .item(item3)
                    .price(new BigDecimal("60.00"))
                    .listingStatus(ListingStatus.ACTIVE)
                    .build();

            listingRepository.saveAll(List.of(listing1, listing2, listing3));

            // ORDERS
            Order order1 = Order.builder()
                    .buyer(customer1)
                    .totalAmount(new BigDecimal("40.00"))
                    .orderStatus(OrderStatus.COMPLETED)
                    .createdAt(Instant.now())
                    .build();

            Order order2 = Order.builder()
                    .buyer(customer2)
                    .totalAmount(new BigDecimal("60.00"))
                    .orderStatus(OrderStatus.PROCESSING)
                    .createdAt(Instant.now())
                    .build();

            orderRepository.saveAll(List.of(order1, order2));

            // NOTIFICATIONS
            Notification n1 = Notification.builder()
                    .user(customer1)
                    .message("Your item has been listed!")
                    .read(false)
                    .type(NotificationType.LISTING_CREATED)
                    .createdAt(Instant.now())
                    .build();

            Notification n2 = Notification.builder()
                    .user(customer1)
                    .message("Your item has been sold!")
                    .read(false)
                    .type(NotificationType.ITEM_SOLD)
                    .createdAt(Instant.now())
                    .build();

            Notification n3 = Notification.builder()
                    .user(customer2)
                    .message("Pickup scheduled successfully.")
                    .read(false)
                    .type(NotificationType.PICKUP_SCHEDULED)
                    .createdAt(Instant.now())
                    .build();

            notificationRepository.saveAll(List.of(n1, n2, n3));
        };
    }
}