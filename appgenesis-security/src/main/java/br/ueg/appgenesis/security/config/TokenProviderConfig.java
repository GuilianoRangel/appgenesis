package br.ueg.appgenesis.security.config;

import br.ueg.appgenesis.security.adapter.auth.JwtTokenProviderAdapter;
import br.ueg.appgenesis.security.port.auth.TokenProviderPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TokenProviderConfig {

    @Bean
    public TokenProviderPort tokenProviderPort(
            @Value("${security.jwt.secret-base64}") String secretBase64,
            @Value("${security.jwt.issuer:triprequest}") String issuer,
            @Value("${security.jwt.audience:triprequest-clients}") String audience,
            @Value("${security.jwt.clock-skew-seconds:60}") long clockSkewSeconds
    ) {
        return new JwtTokenProviderAdapter(secretBase64, issuer, audience, clockSkewSeconds);
    }
}
