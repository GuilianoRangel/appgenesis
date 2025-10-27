package br.ueg.appgenesis.core.domain.validation.annotations;

import java.lang.annotation.*;

/** Validação simples de e-mail (formato). */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DomainEmail {
    String message() default "e-mail inválido";
    /** Regex opcional (caso queira customizar). */
    String pattern() default "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
}
