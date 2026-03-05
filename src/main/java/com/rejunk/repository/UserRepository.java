package com.rejunk.repository;

import com.rejunk.domain.model.User;
import com.rejunk.domain.enums.AccountStatus;
import com.rejunk.domain.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    // Used for login/authentication and to enforce unique emails
    Optional<User> findByEmail(String email);

    // Useful for registration validation
    boolean existsByEmail(String email);

    // Admin panel: list only customers (optional but handy)
    List<User> findAllByRole(UserRole role);

    // Admin panel: list users by account status (active/suspended)
    List<User> findAllByStatus(AccountStatus status);

    // Admin panel: search by email (optional)
    List<User> findByEmailContainingIgnoreCase(String emailPart);
}