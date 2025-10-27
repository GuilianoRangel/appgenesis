package br.ueg.appgenesis.security.usecase;

import br.ueg.appgenesis.core.usecase.GenericCrudService;
import br.ueg.appgenesis.security.domain.Permission;
import br.ueg.appgenesis.security.port.PermissionRepositoryPort;

public class PermissionService extends GenericCrudService<Permission, Long> {
    public PermissionService(PermissionRepositoryPort repository) { super(repository); }
}
