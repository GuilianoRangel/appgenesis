package br.ueg.appgenesis.security.config;

import br.ueg.appgenesis.security.adapter.auth.BCryptPasswordHashAdapter;
import br.ueg.appgenesis.security.port.auth.PasswordHashPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PasswordHashConfig {
    @Bean
    public PasswordHashPort passwordHashPort() {
        return new BCryptPasswordHashAdapter();
    }
}
