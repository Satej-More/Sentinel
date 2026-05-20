package com.satej.fraud.application.auth.port;

public interface PasswordEncoderPort {
    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
