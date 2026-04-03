package com.rejunk.config;

import com.rejunk.security.DbUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SECURITY CONFIG (College-project friendly)
 *
 * OPTION A (DEV ONLY): Permit all requests (no login required).
 * OPTION B (NORMAL): Require login with HTTP Basic + roles.
 *
 * HOW TO SWITCH:
 * - For DEV ONLY: uncomment the block marked "OPTION A" and comment out "OPTION B".
 * - For NORMAL: comment out "OPTION A" and uncomment "OPTION B".
 *
 * NOTE: /health endpoint removed as requested.
 */
/*@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final DbUserDetailsService userDetailsService;

    public SecurityConfig(DbUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // BCrypt password hashing
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Tells Spring Security to use your DB users + BCrypt
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // Allows you to call AuthenticationManager in your AuthController for login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(daoAuthenticationProvider());

        // =========================
        // OPTION A (DEV ONLY)
        // =========================
        // Uncomment this block while developing to disable security for every endpoint.
        // Remove/comment it before your final demo/submission.
        http.authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
        );*/


        // =========================
        // OPTION B (NORMAL)
        // =========================
        // Uncomment this block for your real version:
        // - /auth/** is public (register/login)
        // - everything else requires login
        /*
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .anyRequest().authenticated()
        )
        .httpBasic(Customizer.withDefaults());


        return http.build();
    }
}*/

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final DbUserDetailsService userDetailsService;

    public SecurityConfig(DbUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // Authentication provider
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // Password encoder bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Authentication manager bean
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}