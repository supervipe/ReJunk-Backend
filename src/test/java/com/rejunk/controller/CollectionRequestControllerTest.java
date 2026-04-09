package com.rejunk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejunk.domain.enums.RequestStatus;
import com.rejunk.domain.model.CollectionRequest;
import com.rejunk.dto.collection.CreateCollectionRequest;
import com.rejunk.service.CollectionRequestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest(CollectionRequestController.class)
//@AutoConfigureMockMvc(addFilters = false) // disables security for tests
@WebMvcTest(
        controllers = CollectionRequestController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)

class CollectionRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CollectionRequestService collectionRequestService;

    @Autowired
    private ObjectMapper objectMapper;
    //@MockBean
    //private com.rejunk.security.JwtService jwtService;

    //  CREATE
    @Test
    void shouldCreateCollectionRequest() throws Exception {
        CreateCollectionRequest request = new CreateCollectionRequest();
        CollectionRequest response = new CollectionRequest();
        response.setId(UUID.randomUUID());

        when(collectionRequestService.createRequest(request)).thenReturn(response);

        mockMvc.perform(post("/collection-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // GET ALL
    @Test
    void shouldGetAllRequests() throws Exception {
        when(collectionRequestService.getAllRequests())
                .thenReturn(List.of(new CollectionRequest()));

        mockMvc.perform(get("/collection-requests"))
                .andExpect(status().isOk());
    }

    //  GET BY USER
    @Test
    void shouldGetRequestsByUser() throws Exception {
        UUID userId = UUID.randomUUID();

        when(collectionRequestService.getRequestsByUser(userId))
                .thenReturn(List.of(new CollectionRequest()));

        mockMvc.perform(get("/collection-requests/user/{userId}", userId))
                .andExpect(status().isOk());
    }

    //  GET BY ID
    @Test
    void shouldGetRequestById() throws Exception {
        UUID id = UUID.randomUUID();
        CollectionRequest response = new CollectionRequest();
        response.setId(id);

        when(collectionRequestService.getRequestById(id)).thenReturn(response);

        mockMvc.perform(get("/collection-requests/{id}", id))
                .andExpect(status().isOk());
    }

    //  UPDATE STATUS
    @Test
    void shouldUpdateStatus() throws Exception {
        UUID id = UUID.randomUUID();
        CollectionRequest response = new CollectionRequest();
        response.setId(id);

        when(collectionRequestService.updateStatus(id, RequestStatus.SUBMITTED))
                .thenReturn(response);

        mockMvc.perform(patch("/collection-requests/{id}/status", id)
                        .param("status", "SUBMITTED"))
                .andExpect(status().isOk());
    }

    //  PAY REQUEST
    @Test
    void shouldPayRequest() throws Exception {
        UUID id = UUID.randomUUID();
        CollectionRequest response = new CollectionRequest();

        when(collectionRequestService.payRequest(id)).thenReturn(response);

        mockMvc.perform(patch("/collection-requests/{id}/pay", id))
                .andExpect(status().isOk());
    }

    // CANCEL REQUEST
    @Test
    void shouldCancelRequest() throws Exception {
        UUID id = UUID.randomUUID();
        CollectionRequest response = new CollectionRequest();

        when(collectionRequestService.cancelRequest(id)).thenReturn(response);

        mockMvc.perform(patch("/collection-requests/{id}/cancel", id))
                .andExpect(status().isOk());
    }
}