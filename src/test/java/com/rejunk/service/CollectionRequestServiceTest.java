package com.rejunk.service;

import com.rejunk.domain.enums.NotificationType;
import com.rejunk.domain.enums.PaymentStatus;
import com.rejunk.domain.enums.RequestStatus;
import com.rejunk.domain.model.CollectionRequest;
import com.rejunk.domain.model.User;
import com.rejunk.dto.collection.CreateCollectionRequest;
import com.rejunk.repository.CollectionRequestRepository;
import com.rejunk.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CollectionRequestServiceTest {

    @Mock
    private CollectionRequestRepository collectionRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private CollectionRequestService service;

    private User user;
    private CollectionRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(UUID.randomUUID());

        request = new CollectionRequest();
        request.setId(UUID.randomUUID());
        request.setCustomer(user);
        request.setPickupAddress("1124 Vancouver");
        request.setPickupFee(BigDecimal.TEN);
        request.setPaymentStatus(PaymentStatus.UNPAID);
        request.setRequestStatus(RequestStatus.SUBMITTED);
    }


    // CREATE REQUEST
    @Test
    void createRequest_shouldSaveSuccessfully() {
        CreateCollectionRequest dto = new CreateCollectionRequest();
        dto.setCustomerId(user.getId());
        dto.setPickupAddress("1124 Vancouver");
        dto.setPickupFee(BigDecimal.TEN);
        dto.setPreferredPickupTime(Instant.now());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(collectionRequestRepository.save(any())).thenReturn(request);

        CollectionRequest result = service.createRequest(dto);

        assertNotNull(result);
        assertEquals(user, result.getCustomer());

        verify(collectionRequestRepository).save(any());
    }

    @Test
    void createRequest_shouldThrowIfUserNotFound() {
        CreateCollectionRequest dto = new CreateCollectionRequest();
        dto.setCustomerId(UUID.randomUUID());

        when(userRepository.findById(dto.getCustomerId()))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.createRequest(dto));

        assertEquals("User not found", ex.getMessage());
    }

    // GET REQUESTS BY USER
    @Test
    void getRequestsByUser_shouldReturnList() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(collectionRequestRepository.findByCustomer(user))
                .thenReturn(List.of(request));

        List<CollectionRequest> result = service.getRequestsByUser(user.getId());

        assertEquals(1, result.size());
    }

    // UPDATE STATUS
    @Test
    void updateStatus_shouldUpdateAndNotify() {
        when(collectionRequestRepository.findById(request.getId()))
                .thenReturn(Optional.of(request));
        when(collectionRequestRepository.save(request))
                .thenReturn(request);

        CollectionRequest result =
                service.updateStatus(request.getId(), RequestStatus.COLLECTED);

        assertEquals(RequestStatus.COLLECTED, result.getRequestStatus());

        verify(notificationService).createNotification(
                eq(user.getId()),
                eq(NotificationType.COLLECTION_REQUEST_UPDATED),
                contains("COLLECTED")
        );
    }

    // PAY REQUEST
    @Test
    void payRequest_shouldUpdatePaymentAndStatus() {
        when(collectionRequestRepository.findById(request.getId()))
                .thenReturn(Optional.of(request));
        when(collectionRequestRepository.save(request))
                .thenReturn(request);

        CollectionRequest result = service.payRequest(request.getId());

        assertEquals(PaymentStatus.PAID, result.getPaymentStatus());
        assertEquals(RequestStatus.PAID, result.getRequestStatus());
    }

    @Test
    void payRequest_shouldThrowIfAlreadyPaid() {
        request.setPaymentStatus(PaymentStatus.PAID);

        when(collectionRequestRepository.findById(request.getId()))
                .thenReturn(Optional.of(request));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.payRequest(request.getId()));

        assertEquals("Collection request is already paid", ex.getMessage());
    }

    // CANCEL REQUEST
    @Test
    void cancelRequest_shouldCancelSuccessfully() {
        when(collectionRequestRepository.findById(request.getId()))
                .thenReturn(Optional.of(request));
        when(collectionRequestRepository.save(request))
                .thenReturn(request);

        CollectionRequest result = service.cancelRequest(request.getId());

        assertEquals(RequestStatus.CANCELED, result.getRequestStatus());
    }

    @Test
    void cancelRequest_shouldThrowIfInvalidState() {
        request.setRequestStatus(RequestStatus.COLLECTED);

        when(collectionRequestRepository.findById(request.getId()))
                .thenReturn(Optional.of(request));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.cancelRequest(request.getId()));

        assertEquals("This collection request cannot be canceled", ex.getMessage());
    }

    // GET BY ID
    @Test
    void getRequestById_shouldReturnRequest() {
        when(collectionRequestRepository.findById(request.getId()))
                .thenReturn(Optional.of(request));

        CollectionRequest result = service.getRequestById(request.getId());

        assertNotNull(result);
    }

    @Test
    void getRequestById_shouldThrowIfNotFound() {
        when(collectionRequestRepository.findById(request.getId()))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.getRequestById(request.getId()));

        assertEquals("Collection request not found", ex.getMessage());
    }
}