package br.ueg.appgenesis.security.adapter.web;

import br.ueg.appgenesis.security.usecase.MembershipService;
import br.ueg.appgenesis.security.usecase.AuthorizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/security/membership")
@Tag(name = "AppGenesis - Security - Membership")
public class MembershipController {

    private final MembershipService membershipService;
    private final AuthorizationService authorizationService;

    public MembershipController(MembershipService membershipService, AuthorizationService authorizationService) {
        this.membershipService = membershipService;
        this.authorizationService = authorizationService;
    }

    @PostMapping("/groups/{groupId}/users/{userId}")
    @Operation(summary = "Associa o usuário ao grupo")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Associação realizada (ou já existente)"),
        @ApiResponse(responseCode = "422", description = "Regra de domínio violada",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> assignUserToGroup(@PathVariable Long groupId, @PathVariable Long userId) {
        membershipService.assignUserToGroup(userId, groupId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/groups/{groupId}/users/{userId}")
    @Operation(summary = "Remove o usuário do grupo")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Associação removida (ou já inexistente)"),
        @ApiResponse(responseCode = "422", description = "Regra de domínio violada",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> revokeUserFromGroup(@PathVariable Long groupId, @PathVariable Long userId) {
        membershipService.revokeUserFromGroup(userId, groupId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/groups/{groupId}/permissions/{permissionId}")
    @Operation(summary = "Concede permissão ao grupo")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Permissão concedida (ou já existente)"),
        @ApiResponse(responseCode = "422", description = "Regra de domínio violada",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> grantPermissionToGroup(@PathVariable Long groupId, @PathVariable Long permissionId) {
        membershipService.grantPermissionToGroup(groupId, permissionId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/groups/{groupId}/permissions/{permissionId}")
    @Operation(summary = "Revoga permissão do grupo")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Permissão revogada (ou já inexistente)"),
        @ApiResponse(responseCode = "422", description = "Regra de domínio violada",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> revokePermissionFromGroup(@PathVariable Long groupId, @PathVariable Long permissionId) {
        membershipService.revokePermissionFromGroup(groupId, permissionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{userId}/permissions")
    @Operation(summary = "Lista permissões efetivas do usuário")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de permissões retornada",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)))),
        @ApiResponse(responseCode = "422", description = "Regra de domínio violada",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<String>> permissionsOfUser(@PathVariable Long userId) {
        return ResponseEntity.ok(authorizationService.permissionsOfUser(userId));
    }
}
