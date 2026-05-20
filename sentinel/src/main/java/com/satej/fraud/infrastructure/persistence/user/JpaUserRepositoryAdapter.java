package com.satej.fraud.infrastructure.persistence.user;

import com.satej.fraud.domain.user.User;
import com.satej.fraud.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaUserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository springDataUserRepository;

    @Override
    public User save(User user) {
        UserEntity entity = UserEntity.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .role(user.getRole())
                .build();
        UserEntity savedEntity = springDataUserRepository.save(entity);
        return mapToDomain(savedEntity);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return springDataUserRepository.findByUsername(username)
                .map(this::mapToDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return springDataUserRepository.existsByUsername(username);
    }

    private User mapToDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .role(entity.getRole())
                .build();
    }
}
