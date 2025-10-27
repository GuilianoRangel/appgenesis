package br.ueg.appgenesis.security.domain;

import br.ueg.appgenesis.core.domain.BaseEntity;
import br.ueg.appgenesis.core.domain.annotation.Field;
import br.ueg.appgenesis.core.domain.validation.annotations.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity<Long> {

    @DomainNotBlank
    @DomainSize(min = 3, max = 50)
    @DomainPattern(regexp = "^[a-zA-Z0-9._-]+$", message = "apenas letras, números, ponto, traço e underscore")
    private String username;

    @DomainNotBlank
    @DomainSize(min = 3, max = 120)
    private String fullName;

    @DomainNotNull
    @DomainEmail
    @DomainSize(max = 180)
    private String email;

    private Long departmentId;

    @DomainIn(values = {"ACTIVE", "INACTIVE", "SUSPENDED"})
    private String status;

    @Field(internalData = true)
    private String passwordHash;

    @Field(internalData = true)
    private LocalDateTime lastLoginAt;
}
