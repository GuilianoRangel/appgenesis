package br.ueg.appgenesis.security.adapter.web.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDTO {
    private String username;
    private String fullName;
    private String email;
    private Long departmentId;
    private String status;
}
