package br.ueg.appgenesis.security.config;

import br.ueg.appgenesis.core.security.CredentialContextPort;
import br.ueg.appgenesis.security.port.UserRepositoryPort;
import br.ueg.appgenesis.security.port.auth.PasswordHashPort;
import br.ueg.appgenesis.security.port.auth.RefreshTokenPort;
import br.ueg.appgenesis.security.port.auth.TokenProviderPort;
import br.ueg.appgenesis.security.usecase.AuthenticationProperties;
import br.ueg.appgenesis.security.usecase.AuthenticationService;
import br.ueg.appgenesis.security.usecase.AuthorizationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthUsecaseConfig {

    @Bean
    public AuthenticationProperties authenticationProperties() {
        return new AuthenticationProperties(120L, 7L); // 2h default (pode vir de @Value também)
    }

    @Bean
    public AuthenticationService authenticationService(UserRepositoryPort userRepo,
                                                       PasswordHashPort passwordHashPort,
                                                       TokenProviderPort tokenProviderPort,
                                                       AuthorizationService authorizationService,
                                                       CredentialContextPort credentialContextPort,
                                                       AuthenticationProperties props,
                                                       RefreshTokenPort refreshTokenPort
                                                       ) {
        return new AuthenticationService(
                userRepo, passwordHashPort, tokenProviderPort, authorizationService, credentialContextPort, props, refreshTokenPort
        );
    }
}
