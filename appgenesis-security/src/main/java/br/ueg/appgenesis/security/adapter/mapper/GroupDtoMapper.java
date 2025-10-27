package br.ueg.appgenesis.security.adapter.mapper;
import br.ueg.appgenesis.core.infrastructure.mapper.GenericDtoMapper;
import br.ueg.appgenesis.security.adapter.web.dto.GroupRequestDTO;
import br.ueg.appgenesis.security.adapter.web.dto.GroupResponseDTO;
import br.ueg.appgenesis.security.domain.Group;
import org.mapstruct.*;
@Mapper(componentModel = "spring")
public interface GroupDtoMapper extends GenericDtoMapper<Group, GroupRequestDTO, GroupResponseDTO> {

}
