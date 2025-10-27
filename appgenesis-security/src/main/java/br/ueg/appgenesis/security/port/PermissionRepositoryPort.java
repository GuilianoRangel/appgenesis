package br.ueg.appgenesis.security.port;

import br.ueg.appgenesis.core.port.GenericRepositoryPort;
import br.ueg.appgenesis.security.domain.Permission;

import java.util.Optional;

public interface PermissionRepositoryPort extends GenericRepositoryPort<Permission, Long> {
    Optional<Permission> findByCode(String code);
}
