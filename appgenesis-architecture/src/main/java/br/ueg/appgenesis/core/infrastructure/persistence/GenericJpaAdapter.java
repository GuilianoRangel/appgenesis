package br.ueg.appgenesis.core.infrastructure.persistence;

import br.ueg.appgenesis.core.domain.pagination.PaginatedResult;
import br.ueg.appgenesis.core.domain.pagination.PagingRequest;
import br.ueg.appgenesis.core.domain.pagination.SortDirection;
import br.ueg.appgenesis.core.infrastructure.mapper.GenericEntityMapper;
import br.ueg.appgenesis.core.port.GenericRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class GenericJpaAdapter<T, E, ID> implements GenericRepositoryPort<T, ID> {

    protected final JpaRepository<E, ID> repository;
    protected final GenericEntityMapper<T, E> mapper;

    protected GenericJpaAdapter(JpaRepository<E, ID> repository, GenericEntityMapper<T, E> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public T save(T domain) {
        E entity = repository.save(mapper.toEntity(domain));
        return mapper.toDomain(entity);
    }

    @Override
    public Optional<T> findById(ID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<T> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public PaginatedResult<T> findAll(PagingRequest pagingRequest) {
        Pageable pageable = toPageable(pagingRequest);
        Page<E> entityPage = repository.findAll(pageable);
        return toPaginatedResult(entityPage);
    }

    @Override
    public void deleteById(ID id) {
        repository.deleteById(id);
    }

    private Pageable toPageable(PagingRequest pagingRequest) {
        Sort.Direction direction = pagingRequest.getSortDirection() == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, pagingRequest.getSortBy());
        return PageRequest.of(pagingRequest.getPage(), pagingRequest.getSize(), sort);
    }

    private PaginatedResult<T> toPaginatedResult(Page<E> entityPage) {
        List<T> domainContent = entityPage.getContent().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());

        return PaginatedResult.<T>builder()
                .content(domainContent)
                .totalElements(entityPage.getTotalElements())
                .totalPages(entityPage.getTotalPages())
                .page(entityPage.getNumber())
                .size(entityPage.getSize())
                .first(entityPage.isFirst())
                .last(entityPage.isLast())
                .build();
    }
}
