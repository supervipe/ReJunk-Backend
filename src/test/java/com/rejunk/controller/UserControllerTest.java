package com.rejunk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejunk.domain.model.User;
import com.rejunk.security.JwtService;
import com.rejunk.service.UserService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@WebMvcTest(
        controllers = UserController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private JwtService jwtService;

    @MockBean
    private org.springframework.security.authentication.AuthenticationManager authenticationManager;

    @MockBean
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    @MockBean
    private com.rejunk.security.DbUserDetailsService dbUserDetailsService;

    // GET USER BY ID
    @Test
    void getUser_shouldReturnUser() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setEmail("test@email.com");

        when(userService.getUserById(userId)).thenReturn(user);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value("test@email.com"));
    }

    // GET ALL USERS
    @Test
    void getAllUsers_shouldReturnList() throws Exception {
        User user = new User();
        user.setId(UUID.randomUUID());

        when(userService.getAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    // GET CUSTOMERS
    @Test
    void getCustomers_shouldReturnCustomers() throws Exception {
        User user = new User();
        user.setId(UUID.randomUUID());

        when(userService.getCustomers()).thenReturn(List.of(user));

        mockMvc.perform(get("/users/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    // SUSPEND USER
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

    // ACTIVATE USER
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

    // DELETE USER
    @Test
    void deleteUser_shouldReturnNoContent() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk());
    }
}