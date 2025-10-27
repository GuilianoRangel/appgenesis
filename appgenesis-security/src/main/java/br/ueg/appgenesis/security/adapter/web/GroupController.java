package br.ueg.appgenesis.security.adapter.web;

import br.ueg.appgenesis.core.infrastructure.web.GenericDtoRestController;
import br.ueg.appgenesis.security.adapter.web.dto.GroupRequestDTO;
import br.ueg.appgenesis.security.adapter.web.dto.GroupResponseDTO;
import br.ueg.appgenesis.security.domain.Group;
import br.ueg.appgenesis.security.usecase.GroupService;
import br.ueg.appgenesis.security.adapter.mapper.GroupDtoMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/security/groups")
@Tag(name = "AppGenesis - Security - Groups")
public class GroupController extends GenericDtoRestController<Group, Long, GroupRequestDTO, GroupResponseDTO> {
    public GroupController(GroupService service, GroupDtoMapper mapper) {
        super(service, mapper);
    }
}
