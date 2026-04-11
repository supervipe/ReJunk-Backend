package com.rejunk.service;

import com.rejunk.domain.model.User;
import com.rejunk.repository.UserRepository;
import com.rejunk.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("eve@yahoo.com");
        user.setPasswordHash("encodedPassword");
    }


    // SUCCESS CASE

    @Test
    void loadUserByUsername_shouldReturnUserDetails() {
        when(userRepository.findByEmail("eve@yahoo.com"))
                .thenReturn(Optional.of(user));

        UserDetails result =
                customUserDetailsService.loadUserByUsername("eve@yahoo.com");

        assertNotNull(result);
        assertTrue(result instanceof CustomUserDetails);

        CustomUserDetails custom = (CustomUserDetails) result;

        assertEquals(user.getEmail(), custom.getUsername());
        assertEquals(user.getPasswordHash(), custom.getPassword());
    }


    // FAILURE

    @Test
    void loadUserByUsername_shouldThrowIfUserNotFound() {
        String email = "notfound@example.com";

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(email)
        );

        assertTrue(ex.getMessage().contains(email));
    }
}