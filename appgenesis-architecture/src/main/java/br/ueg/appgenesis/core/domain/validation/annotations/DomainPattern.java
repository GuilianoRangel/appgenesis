package br.ueg.appgenesis.core.domain.validation.annotations;

import java.lang.annotation.*;

/** Garante que o valor (toString) case com o regex informado. */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DomainPattern {
    String regexp();
    String message() default "padrão inválido";
}
