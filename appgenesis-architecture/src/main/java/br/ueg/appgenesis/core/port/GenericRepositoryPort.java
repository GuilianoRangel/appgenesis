package br.ueg.appgenesis.core.port;

import br.ueg.appgenesis.core.domain.pagination.PaginatedResult;
import br.ueg.appgenesis.core.domain.pagination.PagingRequest;

import java.util.List;
import java.util.Optional;

public interface GenericRepositoryPort<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    PaginatedResult<T> findAll(PagingRequest pagingRequest);
    void deleteById(ID id);
}
