package com.satej.fraud.domain.user;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
