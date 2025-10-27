package br.ueg.appgenesis.security.domain;

import br.ueg.appgenesis.core.domain.BaseEntity;
import br.ueg.appgenesis.core.domain.validation.annotations.DomainNotBlank;
import br.ueg.appgenesis.core.domain.validation.annotations.DomainPattern;
import br.ueg.appgenesis.core.domain.validation.annotations.DomainSize;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Permission extends BaseEntity<Long> {
    @DomainNotBlank
    @DomainSize(min = 3, max = 120)
    @DomainPattern(regexp = "^[A-Z_][A-Z0-9_]*$", message = "use UPPER_SNAKE_CASE")
    private String code;

    @DomainSize(max = 255)
    private String description;

    @DomainSize(max = 80)
    private String scope;
}
