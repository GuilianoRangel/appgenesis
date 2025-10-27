package br.ueg.appgenesis.security.domain;

import br.ueg.appgenesis.core.domain.BaseEntity;
import br.ueg.appgenesis.core.domain.validation.annotations.DomainIn;
import br.ueg.appgenesis.core.domain.validation.annotations.DomainNotBlank;
import br.ueg.appgenesis.core.domain.validation.annotations.DomainSize;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Department extends BaseEntity<Long> {
    @DomainNotBlank
    @DomainSize(min = 2, max = 120)
    private String name;

    @DomainSize(max = 255)
    private String description;

    @DomainIn(values = {"ACTIVE","INACTIVE"})
    private String status;

    private Long managerId;
}
