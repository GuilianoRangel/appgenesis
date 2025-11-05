package br.ueg.appgenesis.security.usecase;

import br.ueg.appgenesis.core.domain.security.DomainAuthenticationException;
import br.ueg.appgenesis.core.domain.security.DomainUnauthenticationException;
import br.ueg.appgenesis.core.domain.validation.DomainViolation;
import br.ueg.appgenesis.core.security.CredentialContextPort;
import br.ueg.appgenesis.core.security.CredentialPrincipal;
import br.ueg.appgenesis.security.domain.User;
import br.ueg.appgenesis.security.port.UserRepositoryPort;
import br.ueg.appgenesis.security.port.auth.PasswordHashPort;
import br.ueg.appgenesis.security.port.auth.RefreshTokenPort;
import br.ueg.appgenesis.security.port.auth.TokenProviderPort;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class AuthenticationService {

    private final UserRepositoryPort userRepo;
    private final PasswordHashPort passwordHash;
    private final TokenProviderPort tokenProvider;
    private final AuthorizationService authorizationService;
    private final CredentialContextPort credentialContext;
    private final AuthenticationProperties props;
    private final RefreshTokenPort refreshTokenPort;

    private final java.security.SecureRandom secureRandom = new java.security.SecureRandom();

    public AuthenticationService(UserRepositoryPort userRepo,
                                 PasswordHashPort passwordHash,
                                 TokenProviderPort tokenProvider,
                                 AuthorizationService authorizationService,
                                 CredentialContextPort credentialContext,
                                 AuthenticationProperties props,
                                 RefreshTokenPort refreshTokenPort) {
        this.userRepo = userRepo;
        this.passwordHash = passwordHash;
        this.tokenProvider = tokenProvider;
        this.authorizationService = authorizationService;
        this.credentialContext = credentialContext;
        this.props = (props == null ? new AuthenticationProperties() : props);
        this.refreshTokenPort = refreshTokenPort;
    }

    /** Autentica por usuário/senha e emite token. Lança DomainAuthenticationException (→ 401) em falha. */
    public AuthResult login(String username, String password) {
        var violations = new ArrayList<DomainViolation>();

        var userOpt = userRepo.findByUsername(username);
        if (userOpt.isEmpty()) {
            violations.add(new DomainViolation("username/password", "usuário/senha inválida", "Auth"));
            throw new DomainAuthenticationException(violations);
        }
        User user = userOpt.get();

        if (user.getPasswordHash() == null || !passwordHash.matches(password, user.getPasswordHash())) {
            violations.add(new DomainViolation("username/password", "usuário/senha inválida", "Auth"));
            throw new DomainAuthenticationException(violations);
        }
        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            violations.add(new DomainViolation("status", "usuário inativo/suspenso", "Auth"));
            throw new DomainAuthenticationException(violations);
        }

        // permissões efetivas
        List<String> permissions = authorizationService.permissionsOfUser(user.getId());

        // expiração
        Instant exp = Instant.now().plus(props.getTokenTtlMinutes(), ChronoUnit.MINUTES);

        // claims adicionais (livres)
        Map<String, Object> claims = Map.of(
                "fullName", user.getFullName(),
                "email", user.getEmail()
        );

        // emite accessToken via Port
        String accessToken = tokenProvider.generateToken(
                user.getId(), user.getUsername(), permissions, claims, exp
        );

        // refreshToken
        var refreshToken = issueRefreshToken(user.getId());

        // registra credencial no contexto (opcional; útil p/ fluxos em cadeia no mesmo request)
        credentialContext.setAuthenticatedPrincipal(new SimplePrincipal(user.getId(), user.getUsername(), permissions));

        return new AuthResult(accessToken, exp, refreshToken.token(), refreshToken.expiresAt(), user.getId(), user.getUsername(), user.getFullName(), user.getEmail(), permissions);
    }

    /** Gera token opaco, persiste hash e devolve o par (token claro + expiração). */
    private RefreshPair issueRefreshToken(Long userId) {
        String raw = generateOpaqueToken();                       // ex.: 32 bytes → Base64URL
        Instant exp = Instant.now().plus(props.getRefreshTtlDays(), ChronoUnit.DAYS);
        String hash = sha256(raw);                                // ou PasswordHashPort.encode(raw)
        refreshTokenPort.save(new RefreshTokenPort.RefreshToken(null, userId, hash, exp, false));
        return new RefreshPair(raw, exp);
    }

    private String generateOpaqueToken() {
        byte[] buf = new byte[32];
        secureRandom.nextBytes(buf);
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }
    private String sha256(String s) {
        try {
            var md = java.security.MessageDigest.getInstance("SHA-256");
            return java.util.HexFormat.of().formatHex(md.digest(s.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    /** Fluxo de refresh com rotação atômica (revoga o antigo). */
    public RefreshResult refresh(String refreshTokenClear) {
        if (refreshTokenClear == null || refreshTokenClear.isBlank()) {
            throw new DomainUnauthenticationException("refresh_invalid");
        }
        String hash = sha256(refreshTokenClear); // ou passwordHash.matches strategy
        var stored = refreshTokenPort.findByTokenHash(hash)
                .orElseThrow(() -> new DomainUnauthenticationException("refresh_invalid"));

        if (stored.revoked() || stored.expiresAt().isBefore(Instant.now())) {
            throw new DomainUnauthenticationException("refresh_expired_or_revoked");
        }

        Long userId = stored.userId();
        var user = userRepo.findById(userId)
                .orElseThrow(() -> new DomainUnauthenticationException("user_not_found"));
        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new DomainUnauthenticationException("user_inactive");
        }

        // rotação: revoga o antigo e cria outro
        refreshTokenPort.revoke(stored.id());
        var permissions = authorizationService.permissionsOfUser(userId);
        Instant accessExp = Instant.now().plus(props.getTokenTtlMinutes(), ChronoUnit.MINUTES);
        String access = tokenProvider.generateToken(userId, user.getUsername(), permissions,
                Map.of("fullName", user.getFullName(), "email", user.getEmail()), accessExp);
        var newRefresh = issueRefreshToken(userId);

        return new RefreshResult(access, accessExp, newRefresh.token(), newRefresh.expiresAt());
    }

    public void logoutAll(Long userId) {
        refreshTokenPort.revokeAllForUser(userId);
    }

    /** Retorna o principal autenticado (se houver) baseado no contexto. Útil para /me. */
    public Optional<PrincipalView> currentPrincipal() {
        return credentialContext.getAuthenticatedPrincipal()
                .map(p -> new PrincipalView(p.getUserId(), p.getUsername(), p.getPermissions()));
    }

    /** Valida um token recebido (útil para health/self-check ou introspecção). */
    public Optional<TokenView> introspect(String token) {
        return tokenProvider.parseAndValidate(token).map(payload ->
                new TokenView(payload.userId(), payload.username(), payload.permissions(), payload.expiresAt(), payload.claims()));
    }

    /* ===================== tipos de retorno ===================== */

    /* --- tipos --- */
    public record AuthResult(
            String accessToken, Instant accessExpiresAt,
            String refreshToken, Instant refreshExpiresAt,
            Long userId, String username, String fullName, String email, List<String> permissions) {}

    public record RefreshResult(
            String accessToken, Instant accessExpiresAt,
            String refreshToken, Instant refreshExpiresAt) {}

    private record RefreshPair(String token, Instant expiresAt) {}

    public record PrincipalView(Long userId, String username, List<String> permissions) {}

    public record TokenView(Long userId, String username, List<String> permissions,
                            Instant expiresAt, Map<String, Object> claims) {}

    /* ===================== principal mínimo ===================== */

    public static class SimplePrincipal implements CredentialPrincipal {
        private final Long id; private final String username; private final List<String> perms;
        public SimplePrincipal(Long id, String username, List<String> perms) {
            this.id = id; this.username = username; this.perms = perms == null ? List.of() : perms;
        }
        @Override public Long getUserId() { return id; }
        @Override public String getUsername() { return username; }
        @Override public List<String> getPermissions() { return perms; }
    }
}
