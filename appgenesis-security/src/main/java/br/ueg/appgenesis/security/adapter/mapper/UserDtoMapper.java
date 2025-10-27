package br.ueg.appgenesis.security.adapter.mapper;
import br.ueg.appgenesis.core.infrastructure.mapper.GenericDtoMapper;
import br.ueg.appgenesis.security.adapter.web.dto.UserRequestDTO;
import br.ueg.appgenesis.security.adapter.web.dto.UserResponseDTO;
import br.ueg.appgenesis.security.domain.User;
import org.mapstruct.*;
@Mapper(componentModel = "spring")
public interface UserDtoMapper extends GenericDtoMapper<User, UserRequestDTO, UserResponseDTO> {
}
