package br.ueg.appgenesis.security.adapter.web.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class LoginRequestDTO {
    @Schema(example = "alice")
    private String username;
    @Schema(example = "s3nh4!")
    private String password;
}
