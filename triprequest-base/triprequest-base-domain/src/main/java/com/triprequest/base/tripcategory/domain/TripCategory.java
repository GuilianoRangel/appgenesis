package com.triprequest.base.tripcategory.domain;

import br.ueg.appgenesis.core.domain.BaseEntity;
import br.ueg.appgenesis.core.domain.validation.annotations.DomainNotBlank;
import br.ueg.appgenesis.core.domain.validation.annotations.DomainSize;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TripCategory extends BaseEntity<Long> {

    @DomainNotBlank
    @DomainSize(min = 2, max = 30)
    private String code;

    @DomainNotBlank
    @DomainSize(min = 3, max = 100)
    private String name;

    @DomainSize(max = 255)
    private String description;

    // default será aplicado no use case create
    private Boolean active;
}