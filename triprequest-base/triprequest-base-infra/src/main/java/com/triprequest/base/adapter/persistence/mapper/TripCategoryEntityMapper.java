package com.triprequest.base.adapter.persistence.mapper;

import br.ueg.appgenesis.core.infrastructure.mapper.GenericEntityMapper;
import com.triprequest.base.adapter.persistence.entity.TripCategoryEntity;
import com.triprequest.base.tripcategory.domain.TripCategory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TripCategoryEntityMapper
        extends GenericEntityMapper<TripCategory, TripCategoryEntity> {
}