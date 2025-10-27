package br.ueg.appgenesis.security.adapter.web.dto;
import lombok.*;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class IntrospectResponseDTO {
    private boolean active;
    private Long userId;
    private String username;
    private List<String> permissions;
    private Instant expiresAt;
    private Map<String,Object> claims;
}
