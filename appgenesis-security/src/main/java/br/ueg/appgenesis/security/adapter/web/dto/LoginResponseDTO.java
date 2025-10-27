package br.ueg.appgenesis.security.adapter.web.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.Instant;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LoginResponseDTO {
    @Schema(example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;
    @Schema(example = "2030-01-01T12:00:00Z")
    private Instant accessTokenExpiresAt;
    @Schema(example = "Bearer")
    private String tokenType;
    @Schema(example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;
    @Schema(example = "2030-01-01T12:00:00Z")
    private Instant refreshTokenExpiresAt;

    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private List<String> permissions;
}
