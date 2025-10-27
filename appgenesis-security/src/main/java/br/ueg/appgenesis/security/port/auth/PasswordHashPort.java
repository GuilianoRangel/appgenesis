package br.ueg.appgenesis.security.port.auth;

public interface PasswordHashPort {
    boolean matches(CharSequence raw, String encodedHash);
    String encode(CharSequence raw);
}
