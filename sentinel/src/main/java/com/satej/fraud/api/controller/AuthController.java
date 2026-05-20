package com.satej.fraud.api.controller;

import com.satej.fraud.application.auth.AuthUseCase;
import com.satej.fraud.application.auth.dto.AuthResponse;
import com.satej.fraud.application.auth.dto.LoginRequest;
import com.satej.fraud.application.auth.dto.UserRegistrationRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserRegistrationRequest request) {
        authUseCase.register(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authUseCase.login(request);
        return ResponseEntity.ok(response);
    }
}
