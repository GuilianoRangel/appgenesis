package br.ueg.appgenesis.security.adapter.web;

import br.ueg.appgenesis.core.infrastructure.web.GenericDtoRestController;
import br.ueg.appgenesis.security.adapter.web.dto.PermissionRequestDTO;
import br.ueg.appgenesis.security.adapter.web.dto.PermissionResponseDTO;
import br.ueg.appgenesis.security.domain.Permission;
import br.ueg.appgenesis.security.usecase.PermissionService;
import br.ueg.appgenesis.security.adapter.mapper.PermissionDtoMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/security/permissions")
@Tag(name = "AppGenesis - Security - Permissions")
public class PermissionController extends GenericDtoRestController<Permission, Long, PermissionRequestDTO, PermissionResponseDTO> {
    public PermissionController(PermissionService service, PermissionDtoMapper mapper) {
        super(service, mapper);
    }
}
