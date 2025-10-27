package br.ueg.appgenesis.core.usecase.support;

public enum MergeMode {
    REPLACE_ALL,     // PUT: substitui todos os campos (exceto internos)
    PRESENT_ONLY     // PATCH: apenas campos não-nulos do source (exceto internos)
}
