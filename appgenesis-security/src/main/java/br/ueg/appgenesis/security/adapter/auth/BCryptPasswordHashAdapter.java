package br.ueg.appgenesis.security.adapter.auth;

import br.ueg.appgenesis.security.port.auth.PasswordHashPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptPasswordHashAdapter implements PasswordHashPort {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    @Override public boolean matches(CharSequence raw, String encodedHash) {
        return raw != null && encodedHash != null && encoder.matches(raw, encodedHash);
    }
    @Override public String encode(CharSequence raw) {
        return encoder.encode(raw);
    }
}
