package br.ueg.appgenesis.security.adapter.web;

import br.ueg.appgenesis.core.security.CredentialContextPort;
import br.ueg.appgenesis.security.usecase.ChangePasswordUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "AppGenesis - Security - Change Password", description = "Alterar a senha do próprio usuário")
public class PasswordController {
    private final ChangePasswordUseCase useCase;

    @PostMapping("/me/change-password")
    @Operation(summary = "Altera a própria senha (requer senha atual)")
    @ApiResponse(responseCode = "204", description = "Senha alterada")
    public ResponseEntity<Void> changeMe(@RequestBody ChangeMeRequest req) {
        useCase.changeOwn(req.currentPassword, req.newPassword, req.confirmPassword);
        return ResponseEntity.noContent().build();
    }

    @Data
    public static class ChangeMeRequest {
        public String currentPassword;
        public String newPassword;
        public String confirmPassword;
    }
}
