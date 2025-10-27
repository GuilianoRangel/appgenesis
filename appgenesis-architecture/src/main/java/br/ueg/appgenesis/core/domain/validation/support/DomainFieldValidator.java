package br.ueg.appgenesis.core.domain.validation.support;

import br.ueg.appgenesis.core.domain.validation.DomainValidationException;
import br.ueg.appgenesis.core.domain.validation.DomainViolation;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DomainFieldValidator {

    private DomainFieldValidator() {
        // Utility class
    }

    public static <T> void validateFieldExists(Class<T> domainClass, String fieldName) {
        if (fieldName == null || fieldName.trim().isEmpty()) {
            return; // Não valida se o campo de ordenação não for especificado
        }

        try {
            domainClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            List<String> availableFields = Arrays.stream(domainClass.getDeclaredFields())
                    .map(Field::getName)
                    .toList();
            throw new DomainValidationException(
                    Collections.singletonList(new DomainViolation("sortBy", "Campo de ordenação inválido: '" + fieldName + "'. Campos disponíveis: " + availableFields, "not_exists"))
            );
        }
    }
}