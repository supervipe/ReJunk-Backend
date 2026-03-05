package com.rejunk.controller;

import com.rejunk.domain.model.User;
import com.rejunk.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Register user (simple version)
    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        return userService.registerUser(user);
    }

    // Get user by id
    @GetMapping("/{id}")
    public User getUser(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

    // Get all users (admin usage)
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Get only customers
    @GetMapping("/customers")
    public List<User> getCustomers() {
        return userService.getCustomers();
    }

    // Suspend / ban user
    @PatchMapping("/{id}/suspend")
    public User suspendUser(@PathVariable UUID id) {
        return userService.suspendUser(id);
    }

    // Reactivate user
    @PatchMapping("/{id}/activate")
    public User activateUser(@PathVariable UUID id) {
        return userService.activateUser(id);
    }

    // Delete user
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
    }
}