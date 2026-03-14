package com.rejunk.service;

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

    public CollectionRequestService(CollectionRequestRepository collectionRequestRepository,
                                    UserRepository userRepository) {
        this.collectionRequestRepository = collectionRequestRepository;
        this.userRepository = userRepository;
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

        return collectionRequestRepository.save(request);
    }
}