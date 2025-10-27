package br.ueg.appgenesis.core.usecase.support;

public interface FieldMergeService {
    <T> void merge(T source, T target, MergeMode mode);
}
