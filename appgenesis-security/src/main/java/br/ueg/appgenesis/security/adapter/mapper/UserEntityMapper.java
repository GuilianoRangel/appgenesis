package br.ueg.appgenesis.security.adapter.mapper;

import br.ueg.appgenesis.core.infrastructure.mapper.GenericEntityMapper;
import br.ueg.appgenesis.security.domain.User;
import br.ueg.appgenesis.security.adapter.persistence.entity.UserEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserEntityMapper  extends GenericEntityMapper<User, UserEntity> {
}
