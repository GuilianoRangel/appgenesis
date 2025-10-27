package br.ueg.appgenesis.security.adapter.persistence;

import br.ueg.appgenesis.security.adapter.persistence.entity.RefreshTokenEntity;
import br.ueg.appgenesis.security.adapter.persistence.repository.RefreshTokenJpaRepository;
import br.ueg.appgenesis.security.port.auth.RefreshTokenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RefreshTokenJpaAdapter implements RefreshTokenPort {

    private final RefreshTokenJpaRepository repo;

    private static RefreshTokenPort.RefreshToken toDomain(RefreshTokenEntity e) {
        return new RefreshTokenPort.RefreshToken(
                e.getId(),
                e.getUserId(),
                e.getTokenHash(),
                e.getExpiresAt(),
                e.isRevoked()
        );
    }

    private static void copyToEntity(RefreshTokenPort.RefreshToken d, RefreshTokenEntity e) {
        e.setUserId(d.userId());
        e.setTokenHash(d.tokenHash());
        e.setExpiresAt(d.expiresAt());
        e.setRevoked(d.revoked());
    }

    @Override
    @Transactional
    public RefreshTokenPort.RefreshToken save(RefreshTokenPort.RefreshToken token) {
        RefreshTokenEntity entity;
        if (token.id() == null) {
            entity = RefreshTokenEntity.builder().build();
            copyToEntity(token, entity);
            entity = repo.save(entity);
        } else {
            entity = repo.findById(token.id()).orElseGet(() -> {
                RefreshTokenEntity e = RefreshTokenEntity.builder().id(token.id()).build();
                copyToEntity(token, e);
                return e;
            });
            copyToEntity(token, entity);
            entity = repo.save(entity);
        }
        return toDomain(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RefreshTokenPort.RefreshToken> findByTokenHash(String tokenHash) {
        return repo.findByTokenHash(tokenHash).map(RefreshTokenJpaAdapter::toDomain);
    }

    @Override
    @Transactional
    public void revoke(Long id) {
        // Se não existir, não lança erro (idempotente)
        repo.markRevoked(id);
    }

    @Override
    @Transactional
    public void revokeAllForUser(Long userId) {
        repo.markAllRevokedForUser(userId);
    }
}
