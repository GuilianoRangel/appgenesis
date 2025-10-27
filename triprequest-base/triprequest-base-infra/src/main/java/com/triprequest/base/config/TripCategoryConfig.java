package com.triprequest.base.config;

import br.ueg.appgenesis.core.usecase.PermissionGuard;
import br.ueg.appgenesis.core.usecase.support.AuditableInitializer;
import br.ueg.appgenesis.core.usecase.support.DefaultAuditableInitializer;
import br.ueg.appgenesis.core.usecase.support.FieldMergeService;
import br.ueg.appgenesis.core.usecase.support.ReflectiveFieldMergeService;
import com.triprequest.base.tripcategory.port.TripCategoryRepositoryPort;
import com.triprequest.base.tripcategory.usecase.TripCategoryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TripCategoryConfig {
    @Bean
    public TripCategoryService tripCategoryService(
            TripCategoryRepositoryPort repo,
            PermissionGuard guard
    ) {
        AuditableInitializer aud = new DefaultAuditableInitializer();
        FieldMergeService merge = new ReflectiveFieldMergeService();
        return new TripCategoryService(repo, aud, merge, guard);
    }
}