package br.ueg.appgenesis.core.infrastructure.web;

import br.ueg.appgenesis.core.domain.pagination.PaginatedResult;
import br.ueg.appgenesis.core.domain.pagination.PagingRequest;
import br.ueg.appgenesis.core.domain.pagination.SortDirection;
import br.ueg.appgenesis.core.infrastructure.mapper.GenericDtoMapper;
import br.ueg.appgenesis.core.port.GenericServicePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller genérico (Ports & Adapters):
 * - Recebe RequestDTOs (entrada)
 * - Devolve ResponseDTOs (saída)
 * - Documentado com OpenAPI para suporte a codegen (openapi-generator)
 */
@Validated
public abstract class GenericDtoRestController<D, ID, ReqDTO, ResDTO> {

    protected final GenericServicePort<D, ID> service;
    protected final GenericDtoMapper<D, ReqDTO, ResDTO> dtoMapper;

    protected GenericDtoRestController(GenericServicePort<D, ID> service,
                                       GenericDtoMapper<D, ReqDTO, ResDTO> dtoMapper) {
        this.service = service;
        this.dtoMapper = dtoMapper;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Criar recurso",
            description = "Cria um novo recurso a partir do corpo enviado."
    )
    @ApiResponse(responseCode = "200", description = "Recurso criado", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "Validação falhou",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    public ResponseEntity<ResDTO> create(
            @Valid
            @RequestBody(
                description = "Dados para criação",
                required = true,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE
                )
            )
            @org.springframework.web.bind.annotation.RequestBody ReqDTO dto
    ) {
        D created = service.create(dtoMapper.toDomain(dto));
        return ResponseEntity.ok(dtoMapper.toResponse(created));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Listar recursos",
            description = "Retorna a lista de recursos."
    )
    @ApiResponse(responseCode = "200", description = "Lista retornada", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    public ResponseEntity<List<ResDTO>> findAll() {
        List<ResDTO> out = service.findAll().stream()
                .map(dtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }

    @GetMapping(path = "/paginated", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Listar recursos paginados",
            description = "Retorna uma lista paginada de recursos."
    )
    @ApiResponse(responseCode = "200", description = "Lista paginada retornada", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    public ResponseEntity<PaginatedResult<ResDTO>> findAllPaginated(
            @Parameter(description = "Número da página (0-indexed)", example = "0")
            @RequestParam(name = "page", defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página", example = "20")
            @RequestParam(name = "size", defaultValue = "20") int size,
            @Parameter(description = "Campo para ordenação", example = "id")
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC/DESC)", example = "ASC")
            @RequestParam(name = "sortDirection", defaultValue = "ASC") SortDirection sortDirection
    ) {
        PagingRequest pagingRequest = PagingRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        PaginatedResult<D> domainPaginatedResult = service.findAll(pagingRequest);
        return ResponseEntity.ok(dtoMapper.toPaginatedResponse(domainPaginatedResult));
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Obter por ID",
            description = "Retorna um recurso pelo identificador."
    )
    @ApiResponse(responseCode = "200", description = "Encontrado", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "404", description = "Não encontrado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    public ResponseEntity<ResDTO> find(
            @Parameter(description = "Identificador do recurso", required = true)
            @PathVariable("id") ID id
    ) {
        return ResponseEntity.ok(dtoMapper.toResponse(service.find(id)));
    }

    /**
     * PUT: substituição total do estado público do recurso (exceto campos internos do domínio).
     * Permite null para limpar campos.
     */
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Substituir (PUT)",
            description = "Substitui o estado do recurso pelo corpo enviado (replace). " +
                          "Campos internos anotados no domínio não são sobrescritos."
    )
    @ApiResponse(responseCode = "200", description = "Atualizado", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "404", description = "Não encontrado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    @ApiResponse(responseCode = "400", description = "Validação falhou",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    public ResponseEntity<ResDTO> update(
            @Parameter(description = "Identificador do recurso", required = true)
            @PathVariable("id") ID id,
            @Valid
            @RequestBody(
                description = "Dados completos para substituição (PUT)",
                required = true,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
            @org.springframework.web.bind.annotation.RequestBody ReqDTO dto
    ) {
        D updated = service.update(id, dtoMapper.toDomain(dto)); // mapeia para updateReplace no service
        return ResponseEntity.ok(dtoMapper.toResponse(updated));
    }

    /**
     * PATCH: atualização parcial. Aplica somente campos presentes (não-nulos) no DTO.
     * Ausentes são preservados. Campos internos do domínio não são tocados.
     */
    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Atualizar parcialmente (PATCH)",
            description = "Atualiza apenas os campos presentes no corpo (present-only). " +
                          "Campos internos anotados no domínio não são tocados."
    )
    @ApiResponse(responseCode = "200", description = "Atualizado parcialmente", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "404", description = "Não encontrado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    @ApiResponse(responseCode = "400", description = "Validação falhou",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    public ResponseEntity<ResDTO> patch(
            @Parameter(description = "Identificador do recurso", required = true)
            @PathVariable("id") ID id,
            @Valid
            @RequestBody(
                description = "Dados parciais para atualização (PATCH)",
                required = true,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
            @org.springframework.web.bind.annotation.RequestBody ReqDTO dto
    ) {
        D updated = service.patch(id, dtoMapper.toDomain(dto));
        return ResponseEntity.ok(dtoMapper.toResponse(updated));
    }

    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Excluir",
            description = "Remove o recurso pelo identificador."
    )
    @ApiResponse(responseCode = "204", description = "Removido sem conteúdo")
    @ApiResponse(responseCode = "404", description = "Não encontrado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identificador do recurso", required = true)
            @PathVariable("id") ID id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
