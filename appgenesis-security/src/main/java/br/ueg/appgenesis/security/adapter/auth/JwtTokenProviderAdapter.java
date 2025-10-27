package br.ueg.appgenesis.security.adapter.auth;

import br.ueg.appgenesis.security.port.auth.TokenProviderPort;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.time.Instant;
import java.util.*;

public class JwtTokenProviderAdapter implements TokenProviderPort {

    private final Key signingKey;
    private final String issuer;
    private final String audience;
    private final long clockSkewSeconds;

    // secretBase64: chave Base64 url-safe com pelo menos 256 bits (32 bytes) para HS256
    public JwtTokenProviderAdapter(String secretBase64,
                                   String issuer,
                                   String audience,
                                   long clockSkewSeconds) {
        byte[] keyBytes = Decoders.BASE64.decode(secretBase64);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.issuer = issuer;
        this.audience = audience;
        this.clockSkewSeconds = clockSkewSeconds;
    }

    @Override
    public String generateToken(Long userId, String username, List<String> permissions,
                                Map<String, Object> customClaims, Instant expiresAt) {
        Map<String, Object> claims = new HashMap<>();
        if (customClaims != null) claims.putAll(customClaims);
        claims.put("uid", userId);
        claims.put("perms", permissions == null ? List.of() : permissions);

        JwtBuilder builder = Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(username)
                .setIssuer(issuer)
                .setAudience(audience)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(expiresAt))
                .addClaims(claims)
                .signWith(signingKey, SignatureAlgorithm.HS256);

        return builder.compact();
    }

    @Override
    public ParseResult parse(String token) {
        try {
            JwtParser parser = Jwts.parserBuilder()
                    .requireIssuer(issuer)
                    .requireAudience(audience)
                    .setAllowedClockSkewSeconds(clockSkewSeconds)
                    .setSigningKey(signingKey)
                    .build();

            Jws<Claims> jws = parser.parseClaimsJws(token);
            Claims c = jws.getBody();

            Long uid = c.get("uid", Number.class).longValue();
            String username = c.getSubject();
            @SuppressWarnings("unchecked")
            List<String> perms = (List<String>) c.get("perms");
            if (perms == null) perms = List.of();
            Instant exp = c.getExpiration().toInstant();

            // claims adicionais (sem os padrões)
            Map<String, Object> extras = new HashMap<>(c);
            extras.remove(Claims.SUBJECT);
            extras.remove(Claims.ISSUER);
            extras.remove(Claims.AUDIENCE);
            extras.remove(Claims.EXPIRATION);
            extras.remove(Claims.NOT_BEFORE);
            extras.remove(Claims.ISSUED_AT);
            extras.remove(Claims.ID);

            TokenPayload payload = new TokenPayload(uid, username, perms, exp, extras);
            return new ParseResult(Status.OK, payload);
        } catch (ExpiredJwtException e) {
            return new ParseResult(Status.EXPIRED, null);
        } catch (JwtException | IllegalArgumentException e) {
            return new ParseResult(Status.INVALID, null);
        }
    }
}
