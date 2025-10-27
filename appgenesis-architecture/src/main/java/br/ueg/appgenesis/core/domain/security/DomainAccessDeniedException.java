package br.ueg.appgenesis.core.domain.security;

public class DomainAccessDeniedException extends RuntimeException {
    private final String permission;
    public DomainAccessDeniedException(String permission) { super("access_denied"); this.permission = permission; }
    public String getPermission() { return permission; }
}
