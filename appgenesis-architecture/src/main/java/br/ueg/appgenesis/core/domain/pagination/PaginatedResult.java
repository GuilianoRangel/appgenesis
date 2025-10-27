package br.ueg.appgenesis.core.domain.pagination;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PaginatedResult<T> {
    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int page;
    private int size;
    private boolean first;
    private boolean last;
}