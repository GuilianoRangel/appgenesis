package br.ueg.appgenesis.security.adapter.web.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentRequestDTO {
    private String name;
    private String description;
    private String status;
    private Long managerId;
}
