package br.ueg.appgenesis.core.domain.validation.annotations;

import java.lang.annotation.*;

/** O valor (toString) deve pertencer a um conjunto permitido. */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DomainIn {
    String message() default "valor não permitido";
    String[] values();
    boolean ignoreCase() default true;
}
