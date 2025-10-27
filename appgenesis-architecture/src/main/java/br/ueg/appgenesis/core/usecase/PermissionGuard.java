package br.ueg.appgenesis.core.usecase;

import br.ueg.appgenesis.core.domain.security.DomainAccessDeniedException;
import br.ueg.appgenesis.core.domain.security.DomainAuthenticationException;
import br.ueg.appgenesis.core.security.CredentialContextPort;
import br.ueg.appgenesis.core.security.CredentialPrincipal;

import java.util.List;

public class PermissionGuard {

    private final CredentialContextPort credentialContext;

    public PermissionGuard(CredentialContextPort credentialContext) {
        this.credentialContext = credentialContext;
    }

    public CredentialPrincipal requireAuthenticated() {
        return credentialContext.getAuthenticatedPrincipal()
                .orElseThrow(() -> new DomainAuthenticationException("unauthenticated"));
    }

    public CredentialPrincipal requirePermissions(String... requiredPermissions) {
        CredentialPrincipal p = requireAuthenticated();
        List<String> perms = p.getPermissions();
        for (String need : requiredPermissions) {
            if (!perms.contains(need)) {
                throw new DomainAccessDeniedException(need);
            }
        }
        return p;
    }
}
