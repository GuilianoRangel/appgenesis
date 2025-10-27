package br.ueg.appgenesis.security.adapter.web.dto;

import lombok.*;

import java.util.List;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class UserProfileDTO {

    private UserResponseDTO  user;
    private List<GroupDTO> groups;
    private List<PermissionDTO> permissions;


    @Data @Builder @AllArgsConstructor @NoArgsConstructor
    public static class GroupDTO {
        private Long id;
        private String name;
        private String status;
    }

    @Data @Builder @AllArgsConstructor @NoArgsConstructor
    public static class PermissionDTO {
        private Long id;
        private String code;
        private String description;
    }
}
