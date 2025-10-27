package br.ueg.appgenesis.security.adapter.persistence;

import br.ueg.appgenesis.core.infrastructure.persistence.GenericJpaAdapter;
import br.ueg.appgenesis.security.adapter.persistence.entity.PermissionEntity;
import br.ueg.appgenesis.security.adapter.persistence.repository.PermissionJpaRepository;
import br.ueg.appgenesis.security.domain.Permission;
import br.ueg.appgenesis.security.port.PermissionRepositoryPort;
import br.ueg.appgenesis.security.adapter.mapper.PermissionEntityMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PermissionRepositoryAdapter
        extends GenericJpaAdapter<Permission, PermissionEntity, Long>
        implements PermissionRepositoryPort {

    protected PermissionJpaRepository permissionJpaRepository;

    public PermissionRepositoryAdapter(PermissionJpaRepository repository, PermissionEntityMapper mapper) {
        super(repository, mapper);
        this.permissionJpaRepository = repository;
    }

    @Override public Optional<Permission> findByCode(String code) {
        return permissionJpaRepository.findByCode(code).map(mapper::toDomain);
    }
}
