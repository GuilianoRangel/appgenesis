package br.ueg.appgenesis.core.infrastructure.mapper;

public interface GenericEntityMapper<D, E> extends GenericUpdateMapper<D>{
    D toDomain(E entity);
    E toEntity(D domain);
}
