package br.ueg.appgenesis.core.domain.pagination;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PagingRequest {
    private int page;
    private int size;
    private String sortBy;
    private SortDirection sortDirection;
}