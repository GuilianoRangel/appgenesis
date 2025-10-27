package com.triprequest.base.tripcategory.port;

import br.ueg.appgenesis.core.port.GenericRepositoryPort;
import com.triprequest.base.tripcategory.domain.TripCategory;
import java.util.*;

public interface TripCategoryRepositoryPort extends GenericRepositoryPort<TripCategory, Long> {

    boolean existsByCode(String code);
    Optional<TripCategory> findByCode(String code);
    List<TripCategory> findActiveCategories();
}