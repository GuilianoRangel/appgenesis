package br.ueg.appgenesis.core.domain.validation;

import lombok.Value;

@Value
public class DomainViolation {
    String field;    // ex.: "name"
    String message;  // ex.: "obrigatório" ou "tamanho deve estar entre 3 e 120"
    String code;     // opcional: "NotBlank", "Size"
}
