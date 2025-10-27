package br.ueg.appgenesis.security.domain;

import br.ueg.appgenesis.core.domain.BaseEntity;
import br.ueg.appgenesis.core.domain.validation.annotations.DomainIn;
import br.ueg.appgenesis.core.domain.validation.annotations.DomainNotBlank;
import br.ueg.appgenesis.core.domain.validation.annotations.DomainSize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Group extends BaseEntity<Long> {
    @DomainNotBlank
    @DomainSize(min = 3, max = 80)
    private String name;

    @DomainSize(max = 255)
    private String description;

    @DomainIn(values = {"ACTIVE", "INACTIVE"})
    private String status;
}
