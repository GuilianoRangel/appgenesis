package br.ueg.appgenesis.security.adapter.persistence.link;

import lombok.*;
import java.io.Serializable;

@Data @NoArgsConstructor @AllArgsConstructor
public class GroupPermissionId implements Serializable {
    private Long groupId;
    private Long permissionId;
}
