package br.ueg.appgenesis.security.adapter.mapper;

import br.ueg.appgenesis.core.infrastructure.mapper.GenericEntityMapper;
import br.ueg.appgenesis.security.domain.Group;
import br.ueg.appgenesis.security.adapter.persistence.entity.GroupEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface GroupEntityMapper  extends GenericEntityMapper<Group, GroupEntity> {

}
