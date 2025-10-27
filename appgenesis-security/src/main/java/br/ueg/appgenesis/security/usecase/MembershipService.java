package br.ueg.appgenesis.security.usecase;

import br.ueg.appgenesis.core.domain.validation.DomainValidationException;
import br.ueg.appgenesis.core.domain.validation.DomainViolation;
import br.ueg.appgenesis.core.security.ActionKey;
import br.ueg.appgenesis.core.security.ActionPermissionPolicy;
import br.ueg.appgenesis.core.usecase.ActionSecuredService;
import br.ueg.appgenesis.core.usecase.PermissionGuard;
import br.ueg.appgenesis.security.port.GroupRepositoryPort;
import br.ueg.appgenesis.security.port.PermissionRepositoryPort;
import br.ueg.appgenesis.security.port.UserRepositoryPort;
import br.ueg.appgenesis.security.port.link.GroupPermissionLinkPort;
import br.ueg.appgenesis.security.port.link.UserGroupLinkPort;

import java.util.ArrayList;

public class MembershipService extends ActionSecuredService {

    private static final String MEMBERSHIP_MANAGE = "MEMBERSHIP_MANAGE";

    /**
     * Ações cobertas por este caso de uso.
     */
    public enum MembershipAction implements ActionKey {
        ASSIGN_USER_TO_GROUP,
        REVOKE_USER_FROM_GROUP,
        GRANT_PERMISSION_TO_GROUP,
        REVOKE_PERMISSION_FROM_GROUP
    }

    /**
     * Política de permissões por ação.
     */
    private static final ActionPermissionPolicy POLICY = ActionPermissionPolicy.builder()
            .action(MembershipAction.ASSIGN_USER_TO_GROUP, MEMBERSHIP_MANAGE)
            .action(MembershipAction.REVOKE_USER_FROM_GROUP, MEMBERSHIP_MANAGE)
            .action(MembershipAction.GRANT_PERMISSION_TO_GROUP, MEMBERSHIP_MANAGE)
            .action(MembershipAction.REVOKE_PERMISSION_FROM_GROUP, MEMBERSHIP_MANAGE)
            .build();

    private final UserRepositoryPort userRepo;
    private final GroupRepositoryPort groupRepo;
    private final PermissionRepositoryPort permRepo;
    private final UserGroupLinkPort userGroupLink;
    private final GroupPermissionLinkPort groupPermLink;

    /**
     * Construtor padrão com guard (produção).
     */
    public MembershipService(UserRepositoryPort userRepo,
                             GroupRepositoryPort groupRepo,
                             PermissionRepositoryPort permRepo,
                             UserGroupLinkPort userGroupLink,
                             GroupPermissionLinkPort groupPermLink,
                             PermissionGuard permissionGuard) {
        super(permissionGuard);
        this.userRepo = userRepo;
        this.groupRepo = groupRepo;
        this.permRepo = permRepo;
        this.userGroupLink = userGroupLink;
        this.groupPermLink = groupPermLink;
    }

    /**
     * Construtor sem guard (útil para testes/seed específicos).
     */
    public MembershipService(UserRepositoryPort userRepo,
                             GroupRepositoryPort groupRepo,
                             PermissionRepositoryPort permRepo,
                             UserGroupLinkPort userGroupLink,
                             GroupPermissionLinkPort groupPermLink) {
        this(userRepo, groupRepo, permRepo, userGroupLink, groupPermLink, null);
    }

    @Override
    protected ActionPermissionPolicy permissionPolicy() {
        return POLICY;
    }

    /* =================== Operações com controle de acesso =================== */

    public void assignUserToGroup(Long userId, Long groupId) {
        run(MembershipAction.ASSIGN_USER_TO_GROUP, () -> {
            doAssignUserToGroup(userId, groupId);
        }, userId, groupId);
    }

    private void doAssignUserToGroup(Long userId, Long groupId) {
        var errors = new ArrayList<DomainViolation>();
        userRepo.findById(userId).orElseGet(() -> {
            errors.add(new DomainViolation("userId", "usuário inexistente", "Exists"));
            return null;
        });
        groupRepo.findById(groupId).orElseGet(() -> {
            errors.add(new DomainViolation("groupId", "grupo inexistente", "Exists"));
            return null;
        });
        if (!errors.isEmpty()) throw new DomainValidationException(errors);

        if (!userGroupLink.exists(userId, groupId)) {
            userGroupLink.add(userId, groupId);
        }
    }

    public void revokeUserFromGroup(Long userId, Long groupId) {
        run(MembershipAction.REVOKE_USER_FROM_GROUP, () -> {
            doRevokeUserFromGroup(userId, groupId);
        }, userId, groupId);
    }

    private void doRevokeUserFromGroup(Long userId, Long groupId) {
        if (userGroupLink.exists(userId, groupId)) {
            userGroupLink.remove(userId, groupId);
        }
    }

    public void grantPermissionToGroup(Long groupId, Long permissionId) {
        run(MembershipAction.GRANT_PERMISSION_TO_GROUP, () -> {
            doGrantPermissionToGroup(groupId, permissionId);
        }, groupId, permissionId);
    }

    private void doGrantPermissionToGroup(Long groupId, Long permissionId) {
        var errors = new ArrayList<DomainViolation>();
        groupRepo.findById(groupId).orElseGet(() -> {
            errors.add(new DomainViolation("groupId", "grupo inexistente", "Exists"));
            return null;
        });
        permRepo.findById(permissionId).orElseGet(() -> {
            errors.add(new DomainViolation("permissionId", "permissão inexistente", "Exists"));
            return null;
        });
        if (!errors.isEmpty()) throw new DomainValidationException(errors);

        if (!groupPermLink.exists(groupId, permissionId)) {
            groupPermLink.add(groupId, permissionId);
        }
    }

    public void revokePermissionFromGroup(Long groupId, Long permissionId) {
        run(MembershipAction.REVOKE_PERMISSION_FROM_GROUP, () -> {
            doRevokePermissionFromGroup(groupId, permissionId);
        }, groupId, permissionId);
    }

    private void doRevokePermissionFromGroup(Long groupId, Long permissionId) {
        if (groupPermLink.exists(groupId, permissionId)) {
            groupPermLink.remove(groupId, permissionId);
        }
    }
}
