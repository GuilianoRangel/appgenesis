package com.triprequest.base.adapter.web;

import br.ueg.appgenesis.core.infrastructure.web.GenericDtoRestController;
import com.triprequest.base.adapter.web.dto.*;
import com.triprequest.base.adapter.web.mapper.TripCategoryDtoMapper;
import com.triprequest.base.tripcategory.domain.TripCategory;
import com.triprequest.base.tripcategory.usecase.TripCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/base/trip-categories")
@Tag(name = "Trip Categories", description = "CRUD de categorias de viagem")
public class TripCategoryController extends GenericDtoRestController<TripCategory, Long, TripCategoryRequestDTO, TripCategoryResponseDTO> {
    private final TripCategoryService tripCategoryService;
    public TripCategoryController(TripCategoryService service, TripCategoryDtoMapper mapper) {
        super(service, mapper);
        this.tripCategoryService = service;
    }

    @GetMapping(path = "/actives",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Listar Categorias ativas",
            description = "Retorna a lista de categorias ativas."
    )
    @ApiResponse(responseCode = "200", description = "Lista retornada", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    public ResponseEntity<List<TripCategoryResponseDTO>> findAllActives() {
        List<TripCategoryResponseDTO> out = tripCategoryService.findActiveCategories().stream()
                .map(dtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }
}