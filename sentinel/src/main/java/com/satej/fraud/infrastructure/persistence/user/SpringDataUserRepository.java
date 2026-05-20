package com.satej.fraud.infrastructure.persistence.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SpringDataUserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByUsername(String username);
    boolean existsByUsername(String username);
}
