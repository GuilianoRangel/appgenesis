package br.ueg.appgenesis.security.config;

import br.ueg.appgenesis.security.adapter.persistence.RefreshTokenJpaAdapter;
import br.ueg.appgenesis.security.adapter.persistence.repository.RefreshTokenJpaRepository;
import br.ueg.appgenesis.security.port.auth.RefreshTokenPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RefreshTokenPersistenceConfig {

    @Bean
    public RefreshTokenPort refreshTokenPort(RefreshTokenJpaRepository repo) {
        return new RefreshTokenJpaAdapter(repo);
    }
}
