package br.ueg.appgenesis.security.adapter.web.security;

import br.ueg.appgenesis.core.security.CredentialPrincipal;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.stream.Collectors;

public class SpringUserAuthentication extends AbstractAuthenticationToken {
    private final CredentialPrincipal principal;

    private SpringUserAuthentication(CredentialPrincipal principal) {
        super(principal.getPermissions().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList()));
        this.principal = principal;
        setAuthenticated(true);
    }

    public static SpringUserAuthentication fromPrincipal(CredentialPrincipal p) {
        return new SpringUserAuthentication(p);
    }

    @Override public Object getCredentials() { return ""; }
    @Override public Object getPrincipal() { return principal; }

    public CredentialPrincipal getPrincipalDomain() { return principal; }
}
