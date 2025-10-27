package br.ueg.appgenesis.security.adapter.web.dto;
import lombok.*;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PrincipalResponseDTO {
    private Long userId;
    private String username;
    private List<String> permissions;
}
