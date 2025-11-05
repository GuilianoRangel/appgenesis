package br.ueg.appgenesis.security.adapter.web.config;

import br.ueg.appgenesis.core.security.CredentialContextPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.ueg.appgenesis.security.adapter.web.jwt.JwtAuthenticationFilter;
import br.ueg.appgenesis.security.adapter.web.security.ProblemDetailAccessDeniedHandler;
import br.ueg.appgenesis.security.adapter.web.security.ProblemDetailAuthenticationEntryPoint;
import br.ueg.appgenesis.security.port.auth.TokenProviderPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            TokenProviderPort tokenProvider,
            CredentialContextPort credentialContext,
            ObjectMapper objectMapper) throws Exception {

        var skipPaths = java.util.List.of(
                "**"
        );

        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults()) // <-- desabilita CORS aqui
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(skipPaths.toArray(String[]::new)).permitAll()
                        .anyRequest().authenticated()
                ).exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new ProblemDetailAuthenticationEntryPoint(objectMapper))
                        .accessDeniedHandler(new ProblemDetailAccessDeniedHandler(objectMapper))
                )
                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider, credentialContext, skipPaths),
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var cfg = new org.springframework.web.cors.CorsConfiguration();
        cfg.setAllowedOriginPatterns(java.util.List.of("*")); // ou origens específicas
        cfg.setAllowedMethods(java.util.List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(java.util.List.of("*"));
        cfg.setExposedHeaders(java.util.List.of("Authorization","Location","Link"));
        cfg.setAllowCredentials(false); // true só se precisar enviar cookies
        cfg.setMaxAge(3600L);

        var source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

}