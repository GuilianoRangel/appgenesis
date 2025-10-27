package br.ueg.appgenesis.security.adapter.mapper;

import br.ueg.appgenesis.security.adapter.web.dto.UserProfileDTO;
import br.ueg.appgenesis.security.domain.view.UserProfileView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { UserDtoMapper.class })
public interface UserProfileDtoMapper {

    UserProfileDTO toDTO(UserProfileView view);

    UserProfileDTO.GroupDTO toDTO(UserProfileView.GroupSummary g);
    UserProfileDTO.PermissionDTO toDTO(UserProfileView.PermissionSummary p);
}
