package br.ueg.appgenesis.security.adapter.persistence.repository;

import br.ueg.appgenesis.security.adapter.persistence.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update RefreshTokenEntity r set r.revoked = true where r.id = :id")
    int markRevoked(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update RefreshTokenEntity r set r.revoked = true where r.userId = :userId and r.revoked = false")
    int markAllRevokedForUser(@Param("userId") Long userId);
}
