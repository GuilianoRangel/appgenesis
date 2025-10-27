package br.ueg.appgenesis.core.domain.validation;

import br.ueg.appgenesis.core.domain.validation.annotations.*;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class DomainValidator {
    private DomainValidator() {}

    /** Valida o agregado inteiro e retorna TODAS as violações encontradas. */
    public static List<DomainViolation> validate(Object aggregate) {
        List<DomainViolation> result = new ArrayList<>();
        if (aggregate == null) return result;

        Class<?> c = aggregate.getClass();
        while (c != null && c != Object.class) {
            for (Field f : c.getDeclaredFields()) {
                f.setAccessible(true);
                Object value;
                try { value = f.get(aggregate); } catch (IllegalAccessException e) { continue; }

                // NotNull
                DomainNotNull nn = f.getAnnotation(DomainNotNull.class);
                if (nn != null && value == null) {
                    result.add(new DomainViolation(f.getName(), nn.message(), "NotNull"));
                }

                // NotBlank
                DomainNotBlank nb = f.getAnnotation(DomainNotBlank.class);
                if (nb != null) {
                    if (value == null || value.toString().isBlank()) {
                        result.add(new DomainViolation(f.getName(), nb.message(), "NotBlank"));
                    }
                }

                // @DomainSize
                DomainSize sz = f.getAnnotation(DomainSize.class);
                if (sz != null && value != null) {
                    int len = value.toString().length();
                    if (len < sz.min() || len > sz.max()) {
                        String msg = String.format("tamanho deve estar entre %d e %d", sz.min(), sz.max());
                        result.add(new DomainViolation(f.getName(), msg, "Size"));
                    }
                }

                // Email
                DomainEmail em = f.getAnnotation(DomainEmail.class);
                if (em != null && value != null) {
                    String s = value.toString();
                    if (!Pattern.compile(em.pattern()).matcher(s).matches()) {
                        result.add(new DomainViolation(f.getName(), em.message(), "Email"));
                    }
                }

                // Pattern
                DomainPattern dp = f.getAnnotation(DomainPattern.class);
                if (dp != null && value != null) {
                    String s = value.toString();
                    if (!Pattern.compile(dp.regexp()).matcher(s).matches()) {
                        result.add(new DomainViolation(f.getName(), dp.message(), "Pattern"));
                    }
                }

                // Range
                DomainRange dr = f.getAnnotation(DomainRange.class);
                if (dr != null && value != null) {
                    try {
                        long v = toLong(value);
                        if (v < dr.min() || v > dr.max()) {
                            String msg = String.format("deve estar entre %d e %d", dr.min(), dr.max());
                            result.add(new DomainViolation(f.getName(), msg, "Range"));
                        }
                    } catch (NumberFormatException ex) {
                        result.add(new DomainViolation(f.getName(), "tipo numérico inválido", "RangeType"));
                    }
                }

                // Positive
                DomainPositive pos = f.getAnnotation(DomainPositive.class);
                if (pos != null && value != null) {
                    try {
                        long v = toLong(value);
                        if (v <= 0) {
                            result.add(new DomainViolation(f.getName(), pos.message(), "Positive"));
                        }
                    } catch (NumberFormatException ex) {
                        result.add(new DomainViolation(f.getName(), "tipo numérico inválido", "PositiveType"));
                    }
                }

                // Past
                DomainPast past = f.getAnnotation(DomainPast.class);
                if (past != null && value != null) {
                    if (!isPast(value)) {
                        result.add(new DomainViolation(f.getName(), past.message(), "Past"));
                    }
                }

                // Future
                DomainFuture future = f.getAnnotation(DomainFuture.class);
                if (future != null && value != null) {
                    if (!isFuture(value)) {
                        result.add(new DomainViolation(f.getName(), future.message(), "Future"));
                    }
                }

                // In
                DomainIn in = f.getAnnotation(DomainIn.class);
                if (in != null && value != null) {
                    String s = value.toString();
                    boolean ok = false;
                    for (String allowed : in.values()) {
                        if (in.ignoreCase()) {
                            if (s.equalsIgnoreCase(allowed)) { ok = true; break; }
                        } else {
                            if (s.equals(allowed)) { ok = true; break; }
                        }
                    }
                    if (!ok) {
                        result.add(new DomainViolation(f.getName(), in.message(), "In"));
                    }
                }
            }
            c = c.getSuperclass();
        }
        return result;
    }

    /** Valida e lança DomainValidationException com TODAS as violações. */
    public static void validateOrThrow(Object aggregate) {
        List<DomainViolation> violations = validate(aggregate);
        if (!violations.isEmpty()) throw new DomainValidationException(violations);
    }

    // Helpers

    private static long toLong(Object value) throws NumberFormatException {
        if (value instanceof Number n) {
            if (value instanceof BigDecimal bd) return bd.longValue();
            if (value instanceof BigInteger bi) return bi.longValue();
            return n.longValue();
        }
        return Long.parseLong(value.toString());
    }

    private static boolean isPast(Object value) {
        Instant now = Instant.now();
        if (value instanceof Instant i) return i.isBefore(now);
        if (value instanceof LocalDate d) return d.isBefore(LocalDate.now());
        if (value instanceof LocalDateTime dt) return dt.isBefore(LocalDateTime.now());
        if (value instanceof OffsetDateTime odt) return odt.isBefore(OffsetDateTime.now());
        if (value instanceof ZonedDateTime zdt) return zdt.isBefore(ZonedDateTime.now());
        return false; // tipo não suportado => considera inválido
    }

    private static boolean isFuture(Object value) {
        Instant now = Instant.now();
        if (value instanceof Instant i) return i.isAfter(now);
        if (value instanceof LocalDate d) return d.isAfter(LocalDate.now());
        if (value instanceof LocalDateTime dt) return dt.isAfter(LocalDateTime.now());
        if (value instanceof OffsetDateTime odt) return odt.isAfter(OffsetDateTime.now());
        if (value instanceof ZonedDateTime zdt) return zdt.isAfter(ZonedDateTime.now());
        return false; // tipo não suportado => considera inválido
    }
}
