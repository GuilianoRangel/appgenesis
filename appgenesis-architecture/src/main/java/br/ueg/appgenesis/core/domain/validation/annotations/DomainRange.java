package br.ueg.appgenesis.core.domain.validation.annotations;

import java.lang.annotation.*;

/** Para valores numéricos (Integer, Long, BigDecimal, etc.). */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DomainRange {
    String message() default "fora do intervalo permitido";
    long min() default Long.MIN_VALUE; // inclusive
    long max() default Long.MAX_VALUE; // inclusive
}
