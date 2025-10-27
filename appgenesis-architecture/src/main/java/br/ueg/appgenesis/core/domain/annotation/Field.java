package br.ueg.appgenesis.core.domain.annotation;

import java.lang.annotation.*;

/**
 * Marca atributos do domínio para uso por regras do núcleo (use cases).
 * internalData=true => campo mantido/gerenciado internamente; não deve ser sobrescrito por dados externos.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Field {
    boolean internalData() default false;
}
