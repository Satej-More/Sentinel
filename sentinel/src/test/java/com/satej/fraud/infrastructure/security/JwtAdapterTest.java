package com.satej.fraud.infrastructure.security;

import com.satej.fraud.domain.user.Role;
import com.satej.fraud.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class JwtAdapterTest {

    private JwtAdapter jwtAdapter;
    private final String secret = Base64.getEncoder().encodeToString("super-secure-key-that-is-at-least-256-bits-long-for-hmac-sha256".getBytes());

    @BeforeEach
    void setUp() {
        jwtAdapter = new JwtAdapter();
        ReflectionTestUtils.setField(jwtAdapter, "jwtSecret", secret);
        ReflectionTestUtils.setField(jwtAdapter, "jwtExpirationMs", 3600000L); // 1 hour
        ReflectionTestUtils.setField(jwtAdapter, "jwtRefreshExpirationMs", 86400000L); // 24 hours
    }

    @Test
    void generateAndValidateToken_success() {
        User user = User.builder()
                .id("user-123")
                .username("testuser")
                .role(Role.USER)
                .build();

        String token = jwtAdapter.generateToken(user);
        assertThat(token).isNotEmpty();

        boolean isValid = jwtAdapter.validateToken(token);
        assertThat(isValid).isTrue();

        String username = jwtAdapter.getUsernameFromToken(token);
        assertThat(username).isEqualTo("testuser");
    }

    @Test
    void validateToken_invalidSignature_returnsFalse() {
        boolean isValid = jwtAdapter.validateToken("invalidTokenSignature");
        assertThat(isValid).isFalse();
    }
}
