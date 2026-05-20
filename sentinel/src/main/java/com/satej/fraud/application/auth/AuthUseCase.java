package com.satej.fraud.application.auth;

import com.satej.fraud.application.auth.dto.AuthResponse;
import com.satej.fraud.application.auth.dto.LoginRequest;
import com.satej.fraud.application.auth.dto.UserRegistrationRequest;

public interface AuthUseCase {
    void register(UserRegistrationRequest request);
    AuthResponse login(LoginRequest request);
}
