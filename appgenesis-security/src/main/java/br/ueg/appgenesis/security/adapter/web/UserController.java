package br.ueg.appgenesis.security.adapter.web;

import br.ueg.appgenesis.core.infrastructure.web.GenericDtoRestController;
import br.ueg.appgenesis.security.adapter.mapper.UserProfileDtoMapper;
import br.ueg.appgenesis.security.adapter.web.dto.UserProfileDTO;
import br.ueg.appgenesis.security.adapter.web.dto.UserRequestDTO;
import br.ueg.appgenesis.security.adapter.web.dto.UserResponseDTO;
import br.ueg.appgenesis.security.domain.User;
import br.ueg.appgenesis.security.usecase.UserProfileService;
import br.ueg.appgenesis.security.usecase.UserService;
import br.ueg.appgenesis.security.adapter.mapper.UserDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/security/users")
@Tag(name = "AppGenesis - Security - Users")
public class UserController extends GenericDtoRestController<User, Long, UserRequestDTO, UserResponseDTO> {

    private final UserProfileService userProfileService;
    private final UserProfileDtoMapper userProfileDtoMapper;

    public UserController(UserService service,
                          UserDtoMapper mapper,
                          UserProfileService userProfileService,
                          UserProfileDtoMapper userProfileDtoMapper) {
        super(service, mapper);
        this.userProfileService = userProfileService;
        this.userProfileDtoMapper = userProfileDtoMapper;
    }

    @GetMapping("/{id}/profile")
    @Operation(summary = "Obtém o perfil do usuário (dados + grupos + permissões)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Perfil encontrado"),
                    @ApiResponse(responseCode = "422", description = "Validação de domínio (ex.: usuário inexistente)")
            })
    public ResponseEntity<UserProfileDTO> getProfile(@PathVariable Long id) {
        var view = userProfileService.getProfile(id);
        return ResponseEntity.ok(userProfileDtoMapper.toDTO(view));
    }
}
