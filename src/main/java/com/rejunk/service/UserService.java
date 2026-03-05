package com.rejunk.service;

import com.rejunk.domain.enums.AccountStatus;
import com.rejunk.domain.enums.UserRole;
import com.rejunk.domain.model.User;
import com.rejunk.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Register a new user
    public User registerUser(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        user.setRole(UserRole.CUSTOMER);
        user.setStatus(AccountStatus.ACTIVE);

        return userRepository.save(user);
    }

    // Find user by id
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Find user by email
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Admin: list all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Admin: list only customers
    public List<User> getCustomers() {
        return userRepository.findAllByRole(UserRole.CUSTOMER);
    }

    // Admin: suspend user
    public User suspendUser(UUID userId) {
        User user = getUserById(userId);
        user.setStatus(AccountStatus.SUSPENDED);
        return userRepository.save(user);
    }

    // Admin: reactivate user
    public User activateUser(UUID userId) {
        User user = getUserById(userId);
        user.setStatus(AccountStatus.ACTIVE);
        return userRepository.save(user);
    }

    // Delete user
    public void deleteUser(UUID userId) {
        userRepository.deleteById(userId);
    }
}