package com.triprequest.base.adapter.web.mapper;

import br.ueg.appgenesis.core.infrastructure.mapper.GenericDtoMapper;
import com.triprequest.base.adapter.web.dto.*;
import com.triprequest.base.tripcategory.domain.TripCategory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TripCategoryDtoMapper
        extends GenericDtoMapper<TripCategory, TripCategoryRequestDTO, TripCategoryResponseDTO> {

}