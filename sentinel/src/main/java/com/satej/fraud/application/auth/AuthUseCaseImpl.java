package com.satej.fraud.application.auth;

import com.satej.fraud.application.auth.dto.AuthResponse;
import com.satej.fraud.application.auth.dto.LoginRequest;
import com.satej.fraud.application.auth.dto.UserRegistrationRequest;
import com.satej.fraud.application.auth.port.JwtTokenPort;
import com.satej.fraud.application.auth.port.PasswordEncoderPort;
import com.satej.fraud.domain.user.Role;
import com.satej.fraud.domain.user.User;
import com.satej.fraud.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthUseCaseImpl implements AuthUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final JwtTokenPort jwtTokenPort;

    @Override
    @Transactional
    public void register(UserRegistrationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken!");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.USER)
                .build();

        userRepository.save(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        String token = jwtTokenPort.generateToken(user);
        String refreshToken = jwtTokenPort.generateRefreshToken(user);

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}
