package br.ueg.appgenesis.security.adapter.web.security;

import org.springframework.security.core.AuthenticationException;

public class TokenExpiredAuthException extends AuthenticationException {
    public TokenExpiredAuthException() { super("token_expired"); }
}