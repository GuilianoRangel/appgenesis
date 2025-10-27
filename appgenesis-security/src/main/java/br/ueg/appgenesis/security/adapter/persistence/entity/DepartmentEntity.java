package br.ueg.appgenesis.security.adapter.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name="departments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DepartmentEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, length=120) private String name;
    @Column(length=255) private String description;
    @Column(length=20, nullable=false) private String status;
    @Column(name="manager_id") private Long managerId;
    @Column(name="created_at") private LocalDateTime createdAt;
    @Column(name="updated_at") private LocalDateTime updatedAt;
}
