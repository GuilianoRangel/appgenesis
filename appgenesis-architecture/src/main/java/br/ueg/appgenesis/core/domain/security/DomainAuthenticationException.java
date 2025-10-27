package br.ueg.appgenesis.core.domain.security;

import br.ueg.appgenesis.core.domain.validation.DomainViolation;
import java.util.List;

public class DomainAuthenticationException extends RuntimeException {
    private final List<DomainViolation> violations;

    /** Mensagem simples (sem lista). */
    public DomainAuthenticationException(String message) {
        super(message);
        this.violations = List.of();
    }

    /** Construtor com lista de violações (para 401 com detalhes). */
    public DomainAuthenticationException(List<DomainViolation> violations) {
        super("authentication_failed");
        this.violations = (violations == null ? List.of() : List.copyOf(violations));
    }

    public List<DomainViolation> getViolations() {
        return violations;
    }
}
