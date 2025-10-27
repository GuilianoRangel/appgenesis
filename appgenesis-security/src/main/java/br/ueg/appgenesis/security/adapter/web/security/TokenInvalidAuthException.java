package br.ueg.appgenesis.security.adapter.web.security;

import org.springframework.security.core.AuthenticationException;

public class TokenInvalidAuthException extends AuthenticationException {
    public TokenInvalidAuthException() { super("token_invalid"); }
}