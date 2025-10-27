package br.ueg.appgenesis.security.config;

import br.ueg.appgenesis.core.security.CredentialContextPort;
import br.ueg.appgenesis.core.usecase.PermissionGuard;
import br.ueg.appgenesis.core.usecase.support.AuditableInitializer;
import br.ueg.appgenesis.core.usecase.support.DefaultAuditableInitializer;
import br.ueg.appgenesis.core.usecase.support.FieldMergeService;
import br.ueg.appgenesis.core.usecase.support.ReflectiveFieldMergeService;
import br.ueg.appgenesis.security.port.DepartmentRepositoryPort;
import br.ueg.appgenesis.security.port.GroupRepositoryPort;
import br.ueg.appgenesis.security.port.PermissionRepositoryPort;
import br.ueg.appgenesis.security.port.UserRepositoryPort;
import br.ueg.appgenesis.security.port.link.GroupPermissionLinkPort;
import br.ueg.appgenesis.security.port.link.UserGroupLinkPort;
import br.ueg.appgenesis.security.usecase.*;
import br.ueg.appgenesis.security.port.auth.PasswordHashPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityModuleConfig {
    @Bean
    public AuditableInitializer auditableInitializer() {
        return new DefaultAuditableInitializer();
    }

    @Bean
    public FieldMergeService fieldMergeService() {
        return new ReflectiveFieldMergeService();
    }

    @Bean public DepartmentService departmentService(DepartmentRepositoryPort repo,
                                                     AuditableInitializer auditable,
                                                     FieldMergeService merge,
                                                     PermissionGuard guard) {
        return new DepartmentService(repo, auditable, merge, guard); }

    @Bean public UserService userService(UserRepositoryPort repo,
                                         DepartmentRepositoryPort repoDepartment,
                                         AuditableInitializer auditable,
                                         FieldMergeService merge,
                                         PermissionGuard guard) {
        return new UserService(repo, repoDepartment, auditable, merge, guard); }

    @Bean public GroupService groupService(GroupRepositoryPort repo,
                                           AuditableInitializer auditable,
                                           FieldMergeService merge,
                                           PermissionGuard guard) {
        return new GroupService(repo, auditable, merge, guard); }

    @Bean public PermissionService permissionService(PermissionRepositoryPort repo) {
        return new PermissionService(repo); }

    @Bean public MembershipService membershipService(UserRepositoryPort userRepo,
                                                     GroupRepositoryPort groupRepo,
                                                     PermissionRepositoryPort permRepo,
                                                     UserGroupLinkPort userGroupLink,
                                                     GroupPermissionLinkPort groupPermLink) {
        return new MembershipService(userRepo, groupRepo, permRepo, userGroupLink, groupPermLink);
    }

    @Bean public AuthorizationService authorizationService(UserGroupLinkPort userGroupLink,
                                                           GroupPermissionLinkPort groupPermLink,
                                                           PermissionRepositoryPort permRepo) {
        return new AuthorizationService(userGroupLink, groupPermLink, permRepo);
    }

    @Bean
    public UserProfileService userProfileService(UserRepositoryPort userRepo,
                                                 GroupRepositoryPort groupRepo,
                                                 PermissionRepositoryPort permRepo,
                                                 UserGroupLinkPort userGroupLink,
                                                 GroupPermissionLinkPort groupPermLink) {
        return new UserProfileService(userRepo, groupRepo, permRepo, userGroupLink, groupPermLink);
    }

    /*
    parte de segurança de acesso
     */
    @Bean
    public PermissionGuard permissionGuard(CredentialContextPort credentialContext) {
        return new PermissionGuard(credentialContext);
    }

    @Bean
    public ChangePasswordUseCase changePasswordUseCase(UserRepositoryPort userRepo,
                                                       PasswordHashPort passwordHash,
                                                       CredentialContextPort credentialContext,
                                                       PermissionGuard guard) {
        return new ChangePasswordUseCase(userRepo, passwordHash, credentialContext, guard);
    }
}
