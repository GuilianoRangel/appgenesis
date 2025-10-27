package br.ueg.appgenesis.core.usecase.support;

public interface AuditableInitializer {
    <T> void onCreate(T entity);
    <T> void onUpdate(T entity);
}
