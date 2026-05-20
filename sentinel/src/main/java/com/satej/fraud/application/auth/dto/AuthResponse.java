package com.satej.fraud.application.auth.dto;

import com.satej.fraud.domain.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String refreshToken;
    private String username;
    private Role role;
}
