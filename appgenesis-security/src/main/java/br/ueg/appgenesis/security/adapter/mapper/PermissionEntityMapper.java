package br.ueg.appgenesis.security.adapter.mapper;

import br.ueg.appgenesis.core.infrastructure.mapper.GenericEntityMapper;
import br.ueg.appgenesis.security.domain.Permission;
import br.ueg.appgenesis.security.adapter.persistence.entity.PermissionEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PermissionEntityMapper  extends GenericEntityMapper<Permission, PermissionEntity> {
}
