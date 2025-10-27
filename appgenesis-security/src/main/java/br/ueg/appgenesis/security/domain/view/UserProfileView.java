package br.ueg.appgenesis.security.domain.view;

import br.ueg.appgenesis.security.domain.User;
import lombok.*;
import java.util.List;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class UserProfileView {
    private User user;
    private List<GroupSummary> groups;
    private List<PermissionSummary> permissions;

    @Data @Builder @AllArgsConstructor @NoArgsConstructor
    public static class GroupSummary {
        private Long id;
        private String name;
        private String status;
    }

    @Data @Builder @AllArgsConstructor @NoArgsConstructor
    public static class PermissionSummary {
        private Long id;
        private String code;
        private String description;
    }
}
