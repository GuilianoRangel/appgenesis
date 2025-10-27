package br.ueg.appgenesis.core.domain.validation.annotations;
import java.lang.annotation.*;
@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.FIELD)
public @interface DomainNotBlank { String message() default "obrigatório"; }
