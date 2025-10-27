package br.ueg.appgenesis.core.domain.validation.annotations;
import java.lang.annotation.*;
@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.FIELD)
public @interface DomainSize {
    int min() default 0;
    int max() default Integer.MAX_VALUE;
    String message() default "tamanho inválido";
}
