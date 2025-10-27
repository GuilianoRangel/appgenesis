package br.ueg.appgenesis.core.domain;

import br.ueg.appgenesis.core.domain.annotation.Field;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseEntity<ID> implements Auditable {
    protected ID id;

    @Field(internalData = true)
    protected LocalDateTime createdAt;

    @Field(internalData = true)
    protected LocalDateTime updatedAt;
}
