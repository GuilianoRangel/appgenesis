package br.ueg.appgenesis.security.adapter.web.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionResponseDTO {
    private Long id;
    private String code;
    private String description;
    private String scope;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
