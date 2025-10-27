package br.ueg.appgenesis.security.adapter.mapper;

import br.ueg.appgenesis.core.infrastructure.mapper.GenericEntityMapper;
import br.ueg.appgenesis.security.domain.Department;
import br.ueg.appgenesis.security.adapter.persistence.entity.DepartmentEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface DepartmentEntityMapper  extends GenericEntityMapper<Department, DepartmentEntity> {

}
