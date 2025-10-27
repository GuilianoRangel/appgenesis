package br.ueg.appgenesis.core.infrastructure.mapper;

import org.mapstruct.MappingTarget;

public interface GenericUpdateMapper<D> {
    /**
     * Atualiza o objeto entity com os dados
     * do objeto updateEntity, pegando apenas o atributos
     * preenchidos.
     * @param entity
     * @param updateEntity
     */
    void updateModelFromModel(@MappingTarget D entity, D updateEntity);
}
