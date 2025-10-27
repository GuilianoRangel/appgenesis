package br.ueg.appgenesis.security.adapter.web;

import br.ueg.appgenesis.security.usecase.ChangePasswordUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Tag(name = "AppGenesis - Security - Change User Password (Admin)", description = "Alterar a senha de um usuário")
public class AdminPasswordController {
    private final ChangePasswordUseCase useCase;

    @PutMapping("/{id}/password")
    @Operation(summary = "Altera a senha de outro usuário (não requer senha atual)")
    @ApiResponse(responseCode = "204", description = "Senha alterada")
    public ResponseEntity<Void> changeAny(@PathVariable("id") Long userId,
                                          @RequestBody ChangeAnyRequest req) {
        useCase.changeAny(userId, req.newPassword, req.confirmPassword);
        return ResponseEntity.noContent().build();
    }

    @Data
    public static class ChangeAnyRequest {
        public String newPassword;
        public String confirmPassword;
    }
}
