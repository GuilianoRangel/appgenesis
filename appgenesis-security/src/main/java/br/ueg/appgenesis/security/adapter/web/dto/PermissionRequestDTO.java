package br.ueg.appgenesis.security.adapter.web.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionRequestDTO {
    private String code;
    private String description;
    private String scope;
}
