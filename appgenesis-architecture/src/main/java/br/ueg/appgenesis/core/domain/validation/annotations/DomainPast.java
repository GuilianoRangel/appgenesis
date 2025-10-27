package br.ueg.appgenesis.core.domain.validation.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DomainPast {
    String message() default "deve estar no passado";
}
