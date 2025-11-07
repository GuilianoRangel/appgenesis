package br.ueg.appgenesis.security.adapter.web.jwt;

import br.ueg.appgenesis.core.security.CredentialContextPort;
import br.ueg.appgenesis.core.security.CredentialPrincipal;
import br.ueg.appgenesis.security.adapter.web.security.TokenExpiredAuthException;
import br.ueg.appgenesis.security.adapter.web.security.TokenInvalidAuthException;
import br.ueg.appgenesis.security.port.auth.TokenProviderPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProviderPort tokenProvider;
    private final CredentialContextPort credentialContext;
    private final List<String> skipPatterns; // caminhos que NÃO devem ser filtrados
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final HandlerExceptionResolver resolver;


    public JwtAuthenticationFilter(TokenProviderPort tokenProvider,
                                   CredentialContextPort credentialContext,
                                   List<String> skipPatterns, HandlerExceptionResolver resolver) {
        this.tokenProvider = tokenProvider;
        this.credentialContext = credentialContext;
        this.skipPatterns = (skipPatterns == null ? List.of() : skipPatterns);
        this.resolver = resolver;
    }

    // Ignora o filtro para rotas públicas
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        for (String p : skipPatterns) {
            if (pathMatcher.match(p, path)) return true;
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            var token = header.substring(7);
            var result = tokenProvider.parse(token);

            try{
                switch (result.status()) {
                    case OK -> {
                        var p = result.payload();
                        CredentialPrincipal principal = new CredentialPrincipal() {
                            @Override
                            public Long getUserId() {
                                return p.userId();
                            }

                            @Override
                            public String getUsername() {
                                return p.username();
                            }

                            @Override
                            public List<String> getPermissions() {
                                return p.permissions();
                            }
                        };
                        // registra no contexto via port (agnóstico)
                        credentialContext.setAuthenticatedPrincipal(principal);
                    }
                    case EXPIRED -> throw new TokenExpiredAuthException();
                    case INVALID -> throw new TokenInvalidAuthException();
                }
            }catch(TokenExpiredAuthException|TokenInvalidAuthException ex) {
                //para que o GlogalExceptionHandler consiga tratar exceções Expired e Invalid
                resolver.resolveException(req, res, null, ex);
                // Importante: retornar para não continuar a cadeia
                return;
            }
        }
        chain.doFilter(req, res);
    }
}
