package br.ueg.appgenesis.core.infrastructure.mapper;

/** Mapper genérico para Domain ↔ DTOs (Request/Response). */
import br.ueg.appgenesis.core.domain.pagination.PaginatedResult;

import java.util.List;
import java.util.stream.Collectors;

/** Mapper genérico para Domain ↔ DTOs (Request/Response). */
public interface GenericDtoMapper<D, ReqDTO, ResDTO> {
    D toDomain(ReqDTO dto);
    ResDTO toResponse(D domain);

    default PaginatedResult<ResDTO> toPaginatedResponse(PaginatedResult<D> domainPaginatedResult) {
        List<ResDTO> dtoList = domainPaginatedResult.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return PaginatedResult.<ResDTO>builder()
                .content(dtoList)
                .totalElements(domainPaginatedResult.getTotalElements())
                .totalPages(domainPaginatedResult.getTotalPages())
                .page(domainPaginatedResult.getPage())
                .size(domainPaginatedResult.getSize())
                .first(domainPaginatedResult.isFirst())
                .last(domainPaginatedResult.isLast())
                .build();
    }
}
