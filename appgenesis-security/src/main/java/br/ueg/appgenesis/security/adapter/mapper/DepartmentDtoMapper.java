package br.ueg.appgenesis.security.adapter.mapper;
import br.ueg.appgenesis.core.infrastructure.mapper.GenericDtoMapper;
import br.ueg.appgenesis.security.adapter.web.dto.DepartmentRequestDTO;
import br.ueg.appgenesis.security.adapter.web.dto.DepartmentResponseDTO;
import br.ueg.appgenesis.security.domain.Department;
import org.mapstruct.*;
@Mapper(componentModel = "spring")
public interface DepartmentDtoMapper extends GenericDtoMapper<Department, DepartmentRequestDTO, DepartmentResponseDTO> {
}
