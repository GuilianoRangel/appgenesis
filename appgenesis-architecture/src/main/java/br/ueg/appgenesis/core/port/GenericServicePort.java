package br.ueg.appgenesis.core.port;

import br.ueg.appgenesis.core.domain.pagination.PaginatedResult;
import br.ueg.appgenesis.core.domain.pagination.PagingRequest;

import java.util.List;

public interface GenericServicePort<T, ID> {
    T create(T entity);
    T update(ID id, T entity);
    T patch(ID id, T entity);
    T find(ID id);
    List<T> findAll();
    PaginatedResult<T> findAll(PagingRequest pagingRequest);
    void delete(ID id);
}
