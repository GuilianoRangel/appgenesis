package br.ueg.appgenesis.security.usecase;

import br.ueg.appgenesis.core.security.ActionPermissionPolicy;
import br.ueg.appgenesis.core.usecase.GenericCrudService;
import br.ueg.appgenesis.core.usecase.PermissionGuard;
import br.ueg.appgenesis.core.usecase.support.AuditableInitializer;
import br.ueg.appgenesis.core.usecase.support.FieldMergeService;
import br.ueg.appgenesis.security.domain.Group;
import br.ueg.appgenesis.security.port.GroupRepositoryPort;

public class GroupService extends GenericCrudService<Group, Long> {

    public GroupService(GroupRepositoryPort repository,
                        AuditableInitializer auditable,
                        FieldMergeService merge,
                        PermissionGuard guard) {
        super(repository, auditable, merge, guard); // guard opcional (pode ser null)
    }

    private static final ActionPermissionPolicy POLICY = ActionPermissionPolicy.builder()
            .create("GROUP_WRITE")
            .update("GROUP_WRITE")
            .patch("GROUP_WRITE")
            .delete("GROUP_WRITE")
            .read("GROUP_READ")
            .list("GROUP_READ")
            .build();

    @Override protected ActionPermissionPolicy permissionPolicy() { return POLICY; }

}
