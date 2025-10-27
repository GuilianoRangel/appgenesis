package br.ueg.appgenesis.security.port.auth;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TokenProviderPort {

    enum Status { OK, EXPIRED, INVALID }

    record TokenPayload(Long userId, String username, List<String> permissions,
                        Instant expiresAt, Map<String,Object> claims) {}

    /** Resultado detalhado da validação */
    record ParseResult(Status status, TokenPayload payload) {
        public boolean isOk() { return status == Status.OK && payload != null; }
    }

    String generateToken(Long userId, String username, List<String> permissions,
                         Map<String,Object> customClaims, Instant expiresAt);

    /** Novo: retorna status OK/EXPIRED/INVALID */
    ParseResult parse(String token);

    /** Compat de quem já chama Optional: usa parse() por baixo */
    default Optional<TokenPayload> parseAndValidate(String token) {
        ParseResult r = parse(token);
        return r.isOk() ? Optional.of(r.payload()) : Optional.empty();
    }
}
