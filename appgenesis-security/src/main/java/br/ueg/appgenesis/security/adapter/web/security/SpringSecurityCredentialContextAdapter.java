// com.triprequest.security.adapter.web.security.SpringSecurityCredentialContextAdapter
package br.ueg.appgenesis.security.adapter.web.security;

import br.ueg.appgenesis.core.security.CredentialContextPort;
import br.ueg.appgenesis.core.security.CredentialPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SpringSecurityCredentialContextAdapter implements CredentialContextPort {

    @Override
    public void setAuthenticatedPrincipal(CredentialPrincipal principal) {
        // Nessa abordagem, quem cria o Authentication é o filtro (abaixo).
        // Este método pode não ser necessário se o filtro já colocar no SecurityContext,
        // mas deixamos para atender ao contrato.
        SecurityContextHolder.getContext().setAuthentication(
                SpringUserAuthentication.fromPrincipal(principal)
        );
    }

    @Override
    public Optional<CredentialPrincipal> getAuthenticatedPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof SpringUserAuthentication sua) {
            return Optional.of(sua.getPrincipalDomain());
        }
        return Optional.empty();
    }

    @Override
    public void clear() {
        SecurityContextHolder.clearContext();
    }
}
