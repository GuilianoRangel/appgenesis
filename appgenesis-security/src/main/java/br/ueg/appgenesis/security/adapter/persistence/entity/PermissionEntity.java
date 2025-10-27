package br.ueg.appgenesis.security.adapter.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name="permissions",
    uniqueConstraints = { @UniqueConstraint(name="uk_permissions_code", columnNames="code") })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PermissionEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, length=120) private String code;
    @Column(length=255) private String description;
    @Column(length=80) private String scope;
    @Column(name="created_at") private LocalDateTime createdAt;
    @Column(name="updated_at") private LocalDateTime updatedAt;
}
