package br.ueg.appgenesis.security.adapter.persistence.link;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="group_permission")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@IdClass(GroupPermissionId.class)
public class GroupPermissionEntity {
    @Id @Column(name="group_id") private Long groupId;
    @Id @Column(name="permission_id") private Long permissionId;
}
