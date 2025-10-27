package com.triprequest.base.adapter.persistence.repository;

import com.triprequest.base.adapter.persistence.entity.TripCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TripCategoryJpaRepository extends JpaRepository<TripCategoryEntity, Long> {
    boolean existsByCode(String code);
    Optional<TripCategoryEntity> findByCode(String code);
    List<TripCategoryEntity> findAllByActiveIsTrue();
}