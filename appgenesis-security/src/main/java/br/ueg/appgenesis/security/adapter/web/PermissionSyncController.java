package br.ueg.appgenesis.security.adapter.web;

import br.ueg.appgenesis.security.usecase.PermissionSyncUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/admin/permissions")
@Tag(name = "AppGenesis - Permissions", description = "Sincronização de permissões declaradas nos serviços")
@RequiredArgsConstructor
public class PermissionSyncController {

    private final PermissionSyncUseCase useCase;

    @PostMapping("/sync")
    @Operation(summary = "Sincroniza permissões das policies e associa ao grupo ADMIN informado")
    public ResponseEntity<SyncResultDTO> sync(@RequestBody SyncRequestDTO req) {
        var res = useCase.sync(
                (req.basePackages() == null ? List.of() : req.basePackages()),
                (req.adminGroupName() == null ? "ADMIN" : req.adminGroupName())
        );
        return ResponseEntity.ok(new SyncResultDTO(
                res.adminGroup(), res.serviceToPermissions(), res.allPermissions()
        ));
    }

    /* ===== DTOs ===== */
    public record SyncRequestDTO(List<String> basePackages, String adminGroupName) {}
    public record SyncResultDTO(String adminGroup,
                                Map<String, Set<String>> serviceToPermissions,
                                Set<String> allPermissions) {}
}
