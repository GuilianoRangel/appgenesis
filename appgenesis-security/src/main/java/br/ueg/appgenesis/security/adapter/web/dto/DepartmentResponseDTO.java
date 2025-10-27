package br.ueg.appgenesis.security.adapter.web.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String status;
    private Long managerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
