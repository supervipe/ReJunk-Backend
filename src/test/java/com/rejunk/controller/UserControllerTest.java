package com.rejunk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejunk.domain.model.User;
import com.rejunk.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)


class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    // Get user by ID
    @Test
    void getUser_shouldReturnUser() throws Exception {
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setFullName("John Doe");

        when(userService.getUserById(userId)).thenReturn(user);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.fullName").value("John Doe"));
    }

    //  Get all users
    @Test
    void getAllUsers_shouldReturnList() throws Exception {
        User user1 = new User();
        user1.setFullName("User1");

        User user2 = new User();
        user2.setFullName("User2");

        when(userService.getAllUsers()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // Get customers
    @Test
    void getCustomers_shouldReturnCustomers() throws Exception {
        User user = new User();
        user.setFullName("Customer1");

        when(userService.getCustomers()).thenReturn(List.of(user));

        mockMvc.perform(get("/users/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    //  Suspend user
    @Test
    void suspendUser_shouldReturnUpdatedUser() throws Exception {
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        when(userService.suspendUser(userId)).thenReturn(user);

        mockMvc.perform(patch("/users/{id}/suspend", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()));
    }

    // Activate user
    @Test
    void activateUser_shouldReturnUpdatedUser() throws Exception {
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        when(userService.activateUser(userId)).thenReturn(user);

        mockMvc.perform(patch("/users/{id}/activate", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()));
    }

    //  Delete user
    @Test
    void deleteUser_shouldReturnOk() throws Exception {
        UUID userId = UUID.randomUUID();

        doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk());

        verify(userService).deleteUser(userId);
    }
}



