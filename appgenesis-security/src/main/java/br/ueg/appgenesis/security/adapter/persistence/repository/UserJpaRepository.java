package br.ueg.appgenesis.security.adapter.persistence.repository;

import br.ueg.appgenesis.security.adapter.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    boolean existsByEmail(String email);
}
