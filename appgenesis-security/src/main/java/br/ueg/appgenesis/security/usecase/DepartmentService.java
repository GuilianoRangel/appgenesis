package br.ueg.appgenesis.security.usecase;

import br.ueg.appgenesis.core.security.ActionPermissionPolicy;
import br.ueg.appgenesis.core.usecase.GenericCrudService;
import br.ueg.appgenesis.core.usecase.PermissionGuard;
import br.ueg.appgenesis.core.usecase.support.AuditableInitializer;
import br.ueg.appgenesis.core.usecase.support.FieldMergeService;
import br.ueg.appgenesis.security.domain.Department;
import br.ueg.appgenesis.security.port.DepartmentRepositoryPort;

public class DepartmentService extends GenericCrudService<Department, Long> {
    public DepartmentService(DepartmentRepositoryPort repository,
                             AuditableInitializer auditable,
                             FieldMergeService merge,
                             PermissionGuard guard) {
        super(repository, auditable, merge, guard); // guard opcional (pode ser null)
    }

    private static final ActionPermissionPolicy POLICY = ActionPermissionPolicy.builder()
            .create("DEPARTMENT_WRITE")
            .update("DEPARTMENT_WRITE")
            .patch("DEPARTMENT_WRITE")
            .delete("DEPARTMENT_WRITE")
            .read("DEPARTMENT_READ")
            .list("DEPARTMENT_READ")
            .build();

    @Override protected ActionPermissionPolicy permissionPolicy() { return POLICY; }
}
