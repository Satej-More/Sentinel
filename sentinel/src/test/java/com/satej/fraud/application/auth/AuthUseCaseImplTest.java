package com.satej.fraud.application.auth;

import com.satej.fraud.application.auth.dto.AuthResponse;
import com.satej.fraud.application.auth.dto.LoginRequest;
import com.satej.fraud.application.auth.dto.UserRegistrationRequest;
import com.satej.fraud.application.auth.port.JwtTokenPort;
import com.satej.fraud.application.auth.port.PasswordEncoderPort;
import com.satej.fraud.domain.user.Role;
import com.satej.fraud.domain.user.User;
import com.satej.fraud.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @Mock
    private JwtTokenPort jwtTokenPort;

    private AuthUseCaseImpl authUseCase;

    @BeforeEach
    void setUp() {
        authUseCase = new AuthUseCaseImpl(userRepository, passwordEncoder, jwtTokenPort);
    }

    @Test
    void register_success() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("newuser");
        request.setPassword("password");
        request.setRole(Role.USER);

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("hashedPassword");

        authUseCase.register(request);

        verify(userRepository, times(1)).save(argThat(user -> 
            user.getUsername().equals("newuser") &&
            user.getPassword().equals("hashedPassword") &&
            user.getRole() == Role.USER
        ));
    }

    @Test
    void register_duplicateUsername_throwsException() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("existinguser");

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        assertThatThrownBy(() -> authUseCase.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username is already taken!");

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_success() {
        LoginRequest request = new LoginRequest();
        request.setUsername("user");
        request.setPassword("password");

        User user = User.builder()
                .id("1")
                .username("user")
                .password("hashedPassword")
                .role(Role.USER)
                .build();

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "hashedPassword")).thenReturn(true);
        when(jwtTokenPort.generateToken(user)).thenReturn("jwtToken");
        when(jwtTokenPort.generateRefreshToken(user)).thenReturn("refreshToken");

        AuthResponse response = authUseCase.login(request);

        assertThat(response.getToken()).isEqualTo("jwtToken");
        assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
        assertThat(response.getUsername()).isEqualTo("user");
        assertThat(response.getRole()).isEqualTo(Role.USER);
    }

    @Test
    void login_invalidUsername_throwsException() {
        LoginRequest request = new LoginRequest();
        request.setUsername("unknown");

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authUseCase.login(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid username or password");
    }

    @Test
    void login_invalidPassword_throwsException() {
        LoginRequest request = new LoginRequest();
        request.setUsername("user");
        request.setPassword("wrong");

        User user = User.builder()
                .id("1")
                .username("user")
                .password("hashedPassword")
                .build();

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashedPassword")).thenReturn(false);

        assertThatThrownBy(() -> authUseCase.login(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid username or password");
    }
}
