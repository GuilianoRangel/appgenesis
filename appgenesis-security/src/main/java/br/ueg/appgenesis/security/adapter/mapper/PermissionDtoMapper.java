package br.ueg.appgenesis.security.adapter.mapper;
import br.ueg.appgenesis.core.infrastructure.mapper.GenericDtoMapper;
import br.ueg.appgenesis.security.adapter.web.dto.PermissionRequestDTO;
import br.ueg.appgenesis.security.adapter.web.dto.PermissionResponseDTO;
import br.ueg.appgenesis.security.domain.Permission;
import org.mapstruct.*;
@Mapper(componentModel = "spring")
public interface PermissionDtoMapper extends GenericDtoMapper<Permission, PermissionRequestDTO, PermissionResponseDTO> {
}
