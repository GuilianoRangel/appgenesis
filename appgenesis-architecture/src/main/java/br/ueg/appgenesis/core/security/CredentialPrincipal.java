package br.ueg.appgenesis.core.security;

import java.util.List;

public interface CredentialPrincipal {
    Long getUserId();
    String getUsername();
    List<String> getPermissions();
}
