package br.ueg.appgenesis.core.security;

import java.util.Optional;

public interface CredentialContextPort {
    void setAuthenticatedPrincipal(CredentialPrincipal principal); // registrar (adapter web)
    Optional<CredentialPrincipal> getAuthenticatedPrincipal();      // recuperar (use cases/guard)
    void clear();                                                   // limpar (opcional)
}
