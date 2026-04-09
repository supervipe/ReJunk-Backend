package com.rejunk.service;

import com.rejunk.domain.enums.ListingStatus;
import com.rejunk.domain.enums.PayoutStatus;
import com.rejunk.domain.model.*;
import com.rejunk.dto.payout.CreatePayoutRequest;
import com.rejunk.dto.payout.PayoutResponse;
import com.rejunk.dto.payout.UpdatePayoutStatusRequest;
import com.rejunk.repository.ListingRepository;
import com.rejunk.repository.OrderItemRepository;
import com.rejunk.repository.PayoutRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayoutRecordServiceTest {

    @Mock
    private PayoutRecordRepository payoutRecordRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ListingRepository listingRepository;

    @InjectMocks
    private PayoutRecordService payoutRecordService;

    private OrderItem orderItem;
    private User seller;
    private Listing listing;

    @BeforeEach
    void setUp() {
        seller = User.builder()
                .id(UUID.randomUUID())
                .build();

        CollectionRequest request = CollectionRequest.builder()
                .customer(seller)
                .build();

        Item item = Item.builder()
                .collectionRequest(request)
                .build();

        listing = Listing.builder()
                .id(UUID.randomUUID())
                .item(item)
                .price(new BigDecimal("100.00"))
                .listingStatus(ListingStatus.ACTIVE)
                .build();

        orderItem = new OrderItem();
        orderItem.setId(UUID.randomUUID());
        orderItem.setListing(listing);
        orderItem.setUnitPrice(new BigDecimal("100.00"));
    }

    //  TEST CREATE PAYOUT SUCCESS
    @Test
    void createPayout_shouldCreateSuccessfully() {

        CreatePayoutRequest dto = CreatePayoutRequest.builder()
                .orderItemId(orderItem.getId())
                .platformCommissionPct(new BigDecimal("10"))
                .build();

        when(orderItemRepository.findById(orderItem.getId()))
                .thenReturn(Optional.of(orderItem));

        when(payoutRecordRepository.findByOrderItemId(orderItem.getId()))
                .thenReturn(Optional.empty());

        when(payoutRecordRepository.save(any(PayoutRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PayoutResponse response = payoutRecordService.createPayout(dto);

        assertNotNull(response);
        assertEquals(orderItem.getId(), response.getOrderItemId());
        assertEquals(new BigDecimal("100.00"), response.getSaleAmount());
        assertEquals("PENDING", response.getPayoutStatus());

        // 10% of 100 = 10
        assertEquals(new BigDecimal("10.00"), response.getPlatformAmount());
        assertEquals(new BigDecimal("90.00"), response.getSellerAmount());
    }

    //  TEST DUPLICATE PAYOUT
    @Test
    void createPayout_shouldThrowIfAlreadyExists() {

        CreatePayoutRequest dto = CreatePayoutRequest.builder()
                .orderItemId(orderItem.getId())
                .platformCommissionPct(new BigDecimal("10"))
                .build();

        when(orderItemRepository.findById(orderItem.getId()))
                .thenReturn(Optional.of(orderItem));

        when(payoutRecordRepository.findByOrderItemId(orderItem.getId()))
                .thenReturn(Optional.of(new PayoutRecord()));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> payoutRecordService.createPayout(dto));

        assertEquals("Payout already exists for this order item", ex.getMessage());
    }

    //  TEST ORDER ITEM NOT FOUND
    @Test
    void createPayout_shouldThrowIfOrderItemNotFound() {

        CreatePayoutRequest dto = CreatePayoutRequest.builder()
                .orderItemId(UUID.randomUUID())
                .platformCommissionPct(new BigDecimal("10"))
                .build();

        when(orderItemRepository.findById(dto.getOrderItemId()))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> payoutRecordService.createPayout(dto));

        assertEquals("Order item not found", ex.getMessage());
    }

    // TEST GET BY ID
    @Test
    void getPayoutById_shouldReturnPayout() {

        PayoutRecord payout = PayoutRecord.builder()
                .id(UUID.randomUUID())
                .orderItem(orderItem)
                .seller(seller)
                .saleAmount(new BigDecimal("100.00"))
                .platformAmount(new BigDecimal("10.00"))
                .sellerAmount(new BigDecimal("90.00"))
                .payoutStatus(PayoutStatus.PENDING)
                .build();

        when(payoutRecordRepository.findById(payout.getId()))
                .thenReturn(Optional.of(payout));

        PayoutResponse response = payoutRecordService.getPayoutById(payout.getId());

        assertNotNull(response);
        assertEquals(payout.getId(), response.getPayoutId());
    }

    //  TEST NOT FOUND
    @Test
    void getPayoutById_shouldThrowIfNotFound() {

        UUID id = UUID.randomUUID();

        when(payoutRecordRepository.findById(id))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> payoutRecordService.getPayoutById(id));

        assertEquals("Payout record not found", ex.getMessage());
    }

    // TEST UPDATE STATUS → PROCESSED
    @Test
    void updatePayoutStatus_shouldUpdateAndMarkListingSold() {

        PayoutRecord payout = PayoutRecord.builder()
                .id(UUID.randomUUID())
                .orderItem(orderItem)
                .seller(seller)
                .payoutStatus(PayoutStatus.PENDING)
                .build();

        UpdatePayoutStatusRequest dto = new UpdatePayoutStatusRequest();
        dto.setPayoutStatus(PayoutStatus.PROCESSED);

        when(payoutRecordRepository.findById(payout.getId()))
                .thenReturn(Optional.of(payout));

        when(payoutRecordRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PayoutResponse response =
                payoutRecordService.updatePayoutStatus(payout.getId(), dto);

        assertEquals("PROCESSED", response.getPayoutStatus());

        //  IMPORTANT SIDE EFFECT
        verify(listingRepository).save(any(Listing.class));
    }

    //  TEST GET BY SELLER
    @Test
    void getPayoutsBySeller_shouldReturnList() {

        PayoutRecord payout = PayoutRecord.builder()
                .id(UUID.randomUUID())
                .orderItem(orderItem)
                .seller(seller)
                .saleAmount(new BigDecimal("100.00"))
                .platformAmount(new BigDecimal("10.00"))
                .sellerAmount(new BigDecimal("90.00"))
                .payoutStatus(PayoutStatus.PENDING)
                .build();

        when(payoutRecordRepository.findBySellerId(seller.getId()))
                .thenReturn(List.of(payout));

        List<PayoutResponse> responses =
                payoutRecordService.getPayoutsBySeller(seller.getId());

        assertEquals(1, responses.size());
    }
}