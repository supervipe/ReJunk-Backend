package com.rejunk.service;

import com.rejunk.domain.enums.AccountStatus;
import com.rejunk.domain.enums.UserRole;
import com.rejunk.domain.model.User;
import com.rejunk.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .id(UUID.randomUUID())
                .fullName("John Doe")
                .email("john@example.com")
                .passwordHash("password123")
                .build();
    }

    // Test registering a user successfully
    @Test
    void registerUser_shouldEncodePasswordAndSetDefaults() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(user.getPasswordHash())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User registered = userService.registerUser(user);

        assertThat(registered.getPasswordHash()).isEqualTo("encodedPassword");
        assertThat(registered.getRole()).isEqualTo(UserRole.CUSTOMER);
        assertThat(registered.getStatus()).isEqualTo(AccountStatus.ACTIVE);

        verify(userRepository).existsByEmail(user.getEmail());
        verify(userRepository).save(any(User.class));
    }

    // Test registering user with existing email
    @Test
    void registerUser_shouldThrowIfEmailExists() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.registerUser(user));

        assertThat(ex.getMessage()).isEqualTo("Email already registered");
        verify(userRepository, never()).save(any());
    }

    // Test getUserById successfully
    @Test
    void getUserById_shouldReturnUser() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User found = userService.getUserById(user.getId());
        assertThat(found).isEqualTo(user);
    }

    // Test getUserById not found
    @Test
    void getUserById_shouldThrowIfNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.getUserById(id));

        assertThat(ex.getMessage()).isEqualTo("User not found");
    }

    // Test getUserByEmail successfully
    @Test
    void getUserByEmail_shouldReturnUser() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        User found = userService.getUserByEmail(user.getEmail());
        assertThat(found).isEqualTo(user);
    }

    // Test getUserByEmail not found
    @Test
    void getUserByEmail_shouldThrowIfNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.getUserByEmail(user.getEmail()));

        assertThat(ex.getMessage()).isEqualTo("User not found");
    }

    // Test getAllUsers
    @Test
    void getAllUsers_shouldReturnList() {
        List<User> users = List.of(user);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();
        assertThat(result).hasSize(1).contains(user);
    }

    // Test getCustomers
    @Test
    void getCustomers_shouldReturnOnlyCustomers() {
        List<User> customers = List.of(user);
        when(userRepository.findAllByRole(UserRole.CUSTOMER)).thenReturn(customers);

        List<User> result = userService.getCustomers();
        assertThat(result).hasSize(1).contains(user);
    }

    // Test suspendUser
    @Test
    void suspendUser_shouldSetStatusToSuspended() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User updated = userService.suspendUser(user.getId());

        assertThat(updated.getStatus()).isEqualTo(AccountStatus.SUSPENDED);
    }

    // Test activateUser
    @Test
    void activateUser_shouldSetStatusToActive() {
        user.setStatus(AccountStatus.SUSPENDED);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User updated = userService.activateUser(user.getId());

        assertThat(updated.getStatus()).isEqualTo(AccountStatus.ACTIVE);
    }

    // Test deleteUser
    @Test
    void deleteUser_shouldCallRepositoryDelete() {
        UUID id = user.getId();

        userService.deleteUser(id);

        verify(userRepository).deleteById(id);
    }
}