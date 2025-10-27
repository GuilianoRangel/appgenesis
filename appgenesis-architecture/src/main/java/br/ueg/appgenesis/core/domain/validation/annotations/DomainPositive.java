package br.ueg.appgenesis.core.domain.validation.annotations;

import java.lang.annotation.*;

/** Aceita > 0 (para numéricos). */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DomainPositive {
    String message() default "deve ser positivo";
}
