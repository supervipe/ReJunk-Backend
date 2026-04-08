package com.rejunk.service;

import com.rejunk.domain.enums.NotificationType;
import com.rejunk.domain.enums.PaymentStatus;
import com.rejunk.domain.enums.RequestStatus;
import com.rejunk.domain.model.CollectionRequest;
import com.rejunk.domain.model.User;
import com.rejunk.dto.collection.CreateCollectionRequest;
import com.rejunk.repository.CollectionRequestRepository;
import com.rejunk.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CollectionRequestService {

    private final CollectionRequestRepository collectionRequestRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public CollectionRequestService(CollectionRequestRepository collectionRequestRepository,
                                    UserRepository userRepository,
                                    NotificationService notificationService) {
        this.collectionRequestRepository = collectionRequestRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public CollectionRequest createRequest(CreateCollectionRequest dto) {

        User user = userRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        CollectionRequest request = CollectionRequest.builder()
                .customer(user)
                .pickupAddress(dto.getPickupAddress())
                .preferredPickupTime(dto.getPreferredPickupTime())
                .pickupFee(dto.getPickupFee())
                .paymentStatus(PaymentStatus.UNPAID)
                .requestStatus(RequestStatus.SUBMITTED)
                .build();

        return collectionRequestRepository.save(request);
    }

    public List<CollectionRequest> getRequestsByUser(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return collectionRequestRepository.findByCustomer(user);
    }

    public List<CollectionRequest> getAllRequests() {
        return collectionRequestRepository.findAll();
    }

    public CollectionRequest updateStatus(UUID requestId, RequestStatus status) {

        CollectionRequest request = collectionRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setRequestStatus(status);

        CollectionRequest saved = collectionRequestRepository.save(request);

        notificationService.createNotification(
                saved.getCustomer().getId(),
                NotificationType.COLLECTION_REQUEST_UPDATED,
                "Your collection request status has been updated to " + saved.getRequestStatus() + "."
        );

        return saved;
    }

    public CollectionRequest getRequestById(UUID requestId) {
        return collectionRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Collection request not found"));
    }

    public CollectionRequest payRequest(UUID requestId) {
        CollectionRequest request = collectionRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Collection request not found"));

        if (request.getPaymentStatus() == PaymentStatus.PAID) {
            throw new RuntimeException("Collection request is already paid");
        }

        request.setPaymentStatus(PaymentStatus.PAID);

        if (request.getRequestStatus() == RequestStatus.SUBMITTED) {
            request.setRequestStatus(RequestStatus.PAID);
        }

        return collectionRequestRepository.save(request);
    }

    public CollectionRequest cancelRequest(UUID requestId) {
        CollectionRequest request = collectionRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Collection request not found"));

        if (request.getRequestStatus() == RequestStatus.COLLECTED ||
                request.getRequestStatus() == RequestStatus.EVALUATED ||
                request.getRequestStatus() == RequestStatus.CLOSED ||
                request.getRequestStatus() == RequestStatus.CANCELED) {
            throw new RuntimeException("This collection request cannot be canceled");
        }

        request.setRequestStatus(RequestStatus.CANCELED);

        return collectionRequestRepository.save(request);
    }
}