package br.ueg.appgenesis.security.adapter.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshResponseDTO {
    private String accessToken;
    private Instant accessExpiresAt;
    private String tokenType;          // "Bearer"
    private String refreshToken;       // novo refresh (rotação)
    private Instant refreshExpiresAt;
}
