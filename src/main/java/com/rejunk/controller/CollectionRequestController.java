package com.rejunk.controller;

import com.rejunk.domain.enums.RequestStatus;
import com.rejunk.domain.model.CollectionRequest;
import com.rejunk.dto.collection.CreateCollectionRequest;
import com.rejunk.service.CollectionRequestService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/collection-requests")
public class CollectionRequestController {

    private final CollectionRequestService collectionRequestService;

    public CollectionRequestController(CollectionRequestService collectionRequestService) {
        this.collectionRequestService = collectionRequestService;
    }

    @PostMapping
    public CollectionRequest createRequest(@RequestBody CreateCollectionRequest request) {
        return collectionRequestService.createRequest(request);
    }

    @GetMapping("/user/{userId}")
    public List<CollectionRequest> getUserRequests(@PathVariable UUID userId) {
        return collectionRequestService.getRequestsByUser(userId);
    }

    @GetMapping
    public List<CollectionRequest> getAllRequests() {
        return collectionRequestService.getAllRequests();
    }

    @PatchMapping("/{id}/status")
    public CollectionRequest updateStatus(@PathVariable UUID id,
                                          @RequestParam RequestStatus status) {
        return collectionRequestService.updateStatus(id, status);
    }
}