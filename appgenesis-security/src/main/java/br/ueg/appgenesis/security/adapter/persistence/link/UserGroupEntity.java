package br.ueg.appgenesis.security.adapter.persistence.link;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="user_group")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@IdClass(UserGroupId.class)
public class UserGroupEntity {
    @Id @Column(name="user_id") private Long userId;
    @Id @Column(name="group_id") private Long groupId;
}
