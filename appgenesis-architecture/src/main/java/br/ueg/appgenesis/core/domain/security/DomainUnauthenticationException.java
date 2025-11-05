package br.ueg.appgenesis.core.domain.security;

public class DomainUnauthenticationException extends DomainAuthenticationException {
    public DomainUnauthenticationException(String message) {
        super(message);
    }
}
