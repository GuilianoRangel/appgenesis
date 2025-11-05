package br.ueg.appgenesis.security.adapter.web;

import br.ueg.appgenesis.core.domain.security.DomainAuthenticationException;
import br.ueg.appgenesis.security.adapter.web.dto.*;
import br.ueg.appgenesis.security.usecase.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "AppGenesis - Auth", description = "Autenticação e introspecção de tokens")
public class AuthController {

    private final AuthenticationService authService;

    public AuthController(AuthenticationService authService) { this.authService = authService; }

    @GetMapping(path = "/me",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Retorna o principal autenticado (extraído do contexto)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Principal atual",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PrincipalResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    })
    public ResponseEntity<PrincipalResponseDTO> me() {
        var principal = authService.currentPrincipal()
                .orElseThrow(() -> new DomainAuthenticationException("unauthenticated"));
        return ResponseEntity.ok(PrincipalResponseDTO.builder()
                .userId(principal.userId())
                .username(principal.username())
                .permissions(principal.permissions())
                .build());
    }

    @PostMapping(path = "/introspect",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Valida um token e retorna suas informações (não altera contexto)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultado da introspecção",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = IntrospectResponseDTO.class)))
    })
    public ResponseEntity<IntrospectResponseDTO> introspect(@RequestBody IntrospectRequestDTO req) {
        var viewOpt = authService.introspect(req.getToken());
        if (viewOpt.isEmpty()) {
            return ResponseEntity.ok(IntrospectResponseDTO.builder().active(false).build());
        }
        var v = viewOpt.get();
        return ResponseEntity.ok(IntrospectResponseDTO.builder()
                .active(true)
                .userId(v.userId())
                .username(v.username())
                .permissions(v.permissions())
                .expiresAt(v.expiresAt())
                .claims(v.claims())
                .build());
    }

    @PostMapping(path ="/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Autentica por username/senha e emite access/refresh tokens (JWT)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autenticado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = org.springframework.http.ProblemDetail.class))
            )
    })
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO req) {
        var out = authService.login(req.getUsername(), req.getPassword());
        return ResponseEntity.ok(LoginResponseDTO.builder()
                .accessToken(out.accessToken())
                .tokenType("Bearer")
                .accessTokenExpiresAt(out.accessExpiresAt())
                .userId(out.userId())
                .username(out.username())
                .fullName(out.fullName())
                .email(out.email())
                .permissions(out.permissions())
                .refreshToken(out.refreshToken())
                .refreshTokenExpiresAt(out.refreshExpiresAt())
                .build());
    }

    @PostMapping(path = "/refresh",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Gera novo access token a partir de um refresh token válido (rotação automática)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tokens renovados",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RefreshResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Refresh inválido/expirado",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<RefreshResponseDTO> refresh(@RequestBody RefreshRequestDTO req) {
        var r = authService.refresh(req.getRefreshToken());
        return ResponseEntity.ok(RefreshResponseDTO.builder()
                .accessToken(r.accessToken())
                .accessExpiresAt(r.accessExpiresAt())
                .tokenType("Bearer")
                .refreshToken(r.refreshToken())
                .refreshExpiresAt(r.refreshExpiresAt())
                .build());
    }

    @PostMapping(path = "/logout-all",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Revoga todos os refresh tokens do usuário atual")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Revogados"),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Void> logoutAll() {
        // pega o principal do contexto
        var principal = authService.currentPrincipal()
                .orElseThrow(() -> new DomainAuthenticationException("unauthenticated"));
        authService.logoutAll(principal.userId());
        return ResponseEntity.noContent().build();
    }
}
