package com.triprequest.base.tripcategory.usecase;

import br.ueg.appgenesis.core.security.ActionKey;
import br.ueg.appgenesis.core.security.ActionPermissionPolicy;
import br.ueg.appgenesis.core.security.CrudAction;
import br.ueg.appgenesis.core.usecase.GenericCrudService;
import br.ueg.appgenesis.core.usecase.PermissionGuard;
import br.ueg.appgenesis.core.usecase.support.AuditableInitializer;
import br.ueg.appgenesis.core.usecase.support.FieldMergeService;
import com.triprequest.base.tripcategory.domain.TripCategory;
import com.triprequest.base.tripcategory.port.TripCategoryRepositoryPort;

import java.util.List;
import java.util.function.Supplier;

public class TripCategoryService
        extends GenericCrudService<TripCategory, Long> {

    TripCategoryRepositoryPort tripCategoryRepositoryPort;

    // ação customizada
    public enum TripoCategoryActions implements ActionKey { GET_ACTIVE_CATEGORY }

    public static final class TripCategoryPermissions {
        public static final String TRIP_CATEGORY_READ  = "TRIP_CATEGORY_READ";
        public static final String TRIP_CATEGORY_WRITE = "TRIP_CATEGORY_WRITE";
        public static final String TRIP_CATEGORY_DELETE = "TRIP_CATEGORY_DELETE";
        private TripCategoryPermissions() {}
    }

    private static final ActionPermissionPolicy POLICY = ActionPermissionPolicy.builder()
            .action(CrudAction.READ,  TripCategoryPermissions.TRIP_CATEGORY_READ)
            .action(CrudAction.LIST,  TripCategoryPermissions.TRIP_CATEGORY_READ)
            .action(CrudAction.CREATE,TripCategoryPermissions.TRIP_CATEGORY_WRITE)
            .action(CrudAction.UPDATE,TripCategoryPermissions.TRIP_CATEGORY_WRITE)
            .action(CrudAction.PATCH, TripCategoryPermissions.TRIP_CATEGORY_WRITE)
            .action(CrudAction.DELETE,TripCategoryPermissions.TRIP_CATEGORY_DELETE)
            .action(TripoCategoryActions.GET_ACTIVE_CATEGORY,  null)
            .build();

    public TripCategoryService(TripCategoryRepositoryPort repository,
                               AuditableInitializer auditableInitializer,
                               FieldMergeService fieldMergeService,
                               PermissionGuard permissionGuard) {
        super(repository, auditableInitializer, fieldMergeService, permissionGuard);
        this.tripCategoryRepositoryPort = repository;
    }

    public TripCategoryService(TripCategoryRepositoryPort repository) {
        super(repository);
        this.tripCategoryRepositoryPort = repository;
    }

    @Override protected ActionPermissionPolicy permissionPolicy() { return POLICY; }

    @Override
    protected void before(ActionKey action, Object... args) {
        prepareTripCategoryToCreate(action, args);
    }

    private static void prepareTripCategoryToCreate(ActionKey action, Object[] args) {
        if (action == CrudAction.CREATE && args != null && args.length > 0 && args[0] instanceof TripCategory c) {
            if (c.getActive() == null) c.setActive(Boolean.TRUE);
        }
    }

    public List<TripCategory> findActiveCategories() {
        return execute(TripoCategoryActions.GET_ACTIVE_CATEGORY, this::doFindActiveCategories);
    }

    private List<TripCategory> doFindActiveCategories() {
        return tripCategoryRepositoryPort.findActiveCategories();
    }
}