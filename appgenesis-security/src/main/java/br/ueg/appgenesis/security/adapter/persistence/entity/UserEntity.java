package br.ueg.appgenesis.security.adapter.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name="users",
    uniqueConstraints = {
        @UniqueConstraint(name="uk_users_username", columnNames="username"),
        @UniqueConstraint(name="uk_users_email", columnNames="email")
    })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, length=50) private String username;
    @Column(nullable=false, length=120, name="full_name") private String fullName;
    @Column(nullable=false, length=180) private String email;
    @Column(name="department_id") private Long departmentId;
    @Column(length=20, nullable=false) private String status;
    @Column(name="created_at") private LocalDateTime createdAt;
    @Column(name="updated_at") private LocalDateTime updatedAt;
    @Column(name="password_hash") private String passwordHash;
    @Column(name="last_login_at") private LocalDateTime lastLoginAt;
}
