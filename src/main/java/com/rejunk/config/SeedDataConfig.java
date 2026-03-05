package com.rejunk.config;

import com.rejunk.domain.enums.AccountStatus;
import com.rejunk.domain.enums.UserRole;
import com.rejunk.domain.model.User;
import com.rejunk.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SeedDataConfig {

    @Bean
    CommandLineRunner seedUsers(UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {
            if (!userRepository.existsByEmail("admin@rejunk.com")) {
                User admin = new User();
                admin.setFullName("Admin User");
                admin.setEmail("admin@rejunk.com");
                admin.setPhone("0000000000");
                admin.setPasswordHash(encoder.encode("admin123"));
                admin.setRole(UserRole.ADMIN);
                admin.setStatus(AccountStatus.ACTIVE);
                userRepository.save(admin);
            }

            if (!userRepository.existsByEmail("customer@rejunk.com")) {
                User customer = new User();
                customer.setFullName("Test Customer");
                customer.setEmail("customer@rejunk.com");
                customer.setPhone("1111111111");
                customer.setPasswordHash(encoder.encode("customer123"));
                customer.setRole(UserRole.CUSTOMER);
                customer.setStatus(AccountStatus.ACTIVE);
                userRepository.save(customer);
            }
        };
    }
}