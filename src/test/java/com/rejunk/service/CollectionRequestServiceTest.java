package com.rejunk.service;

import com.rejunk.domain.enums.PaymentStatus;
import com.rejunk.domain.enums.RequestStatus;
import com.rejunk.domain.model.CollectionRequest;
import com.rejunk.domain.model.User;
import com.rejunk.dto.collection.CreateCollectionRequest;
import com.rejunk.repository.CollectionRequestRepository;
import com.rejunk.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CollectionRequestServiceTest {

    @Mock
    private CollectionRequestRepository collectionRequestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CollectionRequestService service;

    // ✅ createRequest success
    @Test
    void createRequest_shouldCreateSuccessfully() {
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        CreateCollectionRequest dto = CreateCollectionRequest.builder()
                .customerId(userId)
                .pickupAddress("123 Main St")
                .preferredPickupTime(Instant.now())
                .pickupFee(new BigDecimal("10.00"))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(collectionRequestRepository.save(any(CollectionRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CollectionRequest result = service.createRequest(dto);

        assertNotNull(result);
        assertEquals("123 Main St", result.getPickupAddress());
        assertEquals(PaymentStatus.UNPAID, result.getPaymentStatus());
        assertEquals(RequestStatus.SUBMITTED, result.getRequestStatus());
        assertEquals(user, result.getCustomer());

        verify(collectionRequestRepository).save(any(CollectionRequest.class));
    }

    // createRequest user not found
    @Test
    void createRequest_shouldThrowIfUserNotFound() {
        UUID userId = UUID.randomUUID();

        CreateCollectionRequest dto = CreateCollectionRequest.builder()
                .customerId(userId)
                .pickupAddress("123 Main St")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.createRequest(dto));

        assertEquals("User not found", ex.getMessage());
    }

    // getRequestsByUser success
    @Test
    void getRequestsByUser_shouldReturnList() {
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        List<CollectionRequest> requests = List.of(new CollectionRequest(), new CollectionRequest());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(collectionRequestRepository.findByCustomer(user)).thenReturn(requests);

        List<CollectionRequest> result = service.getRequestsByUser(userId);

        assertEquals(2, result.size());
        verify(collectionRequestRepository).findByCustomer(user);
    }

    // getRequestsByUser user not found
    @Test
    void getRequestsByUser_shouldThrowIfUserNotFound() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.getRequestsByUser(userId));

        assertEquals("User not found", ex.getMessage());
    }

    // getAllRequests
    @Test
    void getAllRequests_shouldReturnAll() {
        List<CollectionRequest> requests = List.of(new CollectionRequest(), new CollectionRequest());

        when(collectionRequestRepository.findAll()).thenReturn(requests);

        List<CollectionRequest> result = service.getAllRequests();

        assertEquals(2, result.size());
    }

    // updateStatus success
    @Test
    void updateStatus_shouldUpdateSuccessfully() {
        UUID requestId = UUID.randomUUID();

        CollectionRequest request = new CollectionRequest();
        request.setId(requestId);

        when(collectionRequestRepository.findById(requestId))
                .thenReturn(Optional.of(request));

        when(collectionRequestRepository.save(any(CollectionRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CollectionRequest result = service.updateStatus(requestId, RequestStatus.SUBMITTED);

        assertEquals(RequestStatus.SUBMITTED, result.getRequestStatus());
        verify(collectionRequestRepository).save(request);
    }

    // updateStatus not found
    @Test
    void updateStatus_shouldThrowIfNotFound() {
        UUID requestId = UUID.randomUUID();

        when(collectionRequestRepository.findById(requestId))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.updateStatus(requestId, RequestStatus.SUBMITTED));

        assertEquals("Request not found", ex.getMessage());
    }
}