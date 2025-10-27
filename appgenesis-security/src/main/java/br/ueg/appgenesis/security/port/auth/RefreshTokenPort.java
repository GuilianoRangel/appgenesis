package br.ueg.appgenesis.security.port.auth;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenPort {
    record RefreshToken(
            Long id,
            Long userId,
            String tokenHash,     // hash do token opaco (não guardar em claro)
            Instant expiresAt,
            boolean revoked
    ) {}

    RefreshToken save(RefreshToken token);                         // criar/atualizar
    Optional<RefreshToken> findByTokenHash(String tokenHash);      // busca por hash
    void revoke(Long id);                                          // revoga 1
    void revokeAllForUser(Long userId);                            // opcional (logout all)
}
