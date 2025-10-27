package com.triprequest.base.adapter.persistence;

import br.ueg.appgenesis.core.infrastructure.persistence.GenericJpaAdapter;
import com.triprequest.base.adapter.persistence.entity.TripCategoryEntity;
import com.triprequest.base.adapter.persistence.mapper.TripCategoryEntityMapper;
import com.triprequest.base.adapter.persistence.repository.TripCategoryJpaRepository;
import com.triprequest.base.tripcategory.domain.TripCategory;
import com.triprequest.base.tripcategory.port.TripCategoryRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class TripCategoryRepositoryAdapter
        extends GenericJpaAdapter<TripCategory, TripCategoryEntity, Long>
        implements TripCategoryRepositoryPort {

    protected TripCategoryJpaRepository tripCategoryJpaRepository;

    public TripCategoryRepositoryAdapter(TripCategoryJpaRepository repository, TripCategoryEntityMapper mapper) {
        super(repository, mapper);
        this.tripCategoryJpaRepository = repository;
    }

    @Override
    public boolean existsByCode(String code) {
        return tripCategoryJpaRepository.existsByCode(code);
    }

    @Override
    public Optional<TripCategory> findByCode(String code) {
        return tripCategoryJpaRepository.findByCode(code).map(mapper::toDomain);
    }

    @Override
    public List<TripCategory> findActiveCategories(){
        return tripCategoryJpaRepository.findAllByActiveIsTrue().stream().map(mapper::toDomain).collect(Collectors.toList());
    }
}