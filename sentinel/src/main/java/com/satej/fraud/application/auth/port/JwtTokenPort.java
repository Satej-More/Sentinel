package com.satej.fraud.application.auth.port;

import com.satej.fraud.domain.user.User;

public interface JwtTokenPort {
    String generateToken(User user);
    String generateRefreshToken(User user);
    boolean validateToken(String token);
    String getUsernameFromToken(String token);
}
