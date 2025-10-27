package br.ueg.appgenesis.security.usecase;

import br.ueg.appgenesis.core.domain.error.NotFoundException;
import br.ueg.appgenesis.core.domain.security.DomainAccessDeniedException;
import br.ueg.appgenesis.core.domain.security.DomainAuthenticationException;
import br.ueg.appgenesis.core.domain.validation.DomainValidationException;
import br.ueg.appgenesis.core.domain.validation.DomainViolation;
import br.ueg.appgenesis.core.security.ActionKey;
import br.ueg.appgenesis.core.security.ActionPermissionPolicy;
import br.ueg.appgenesis.core.security.CredentialContextPort;
import br.ueg.appgenesis.core.usecase.ActionSecuredService;
import br.ueg.appgenesis.core.usecase.PermissionGuard;
import br.ueg.appgenesis.security.domain.User;
import br.ueg.appgenesis.security.port.UserRepositoryPort;
import br.ueg.appgenesis.security.port.auth.PasswordHashPort;

import java.util.ArrayList;
import java.util.Objects;



public class ChangePasswordUseCase extends ActionSecuredService {

    public enum PasswordAction implements ActionKey {
        CHANGE_OWN_PASSWORD,
        CHANGE_ANY_PASSWORD
    }

    public static final class Permissions {
        public static final String USER_CHANGE_OWN_PASSWORD  = "USER_CHANGE_OWN_PASSWORD";
        public static final String USER_CHANGE_ANY_PASSWORD  = "USER_CHANGE_ANY_PASSWORD"; // NOVA
        private Permissions() {}
    }

    private static final ActionPermissionPolicy POLICY = ActionPermissionPolicy.builder()
            .action(PasswordAction.CHANGE_OWN_PASSWORD, Permissions.USER_CHANGE_OWN_PASSWORD)
            .action(PasswordAction.CHANGE_ANY_PASSWORD, Permissions.USER_CHANGE_ANY_PASSWORD)
            .build();

    private final UserRepositoryPort userRepo;
    private final PasswordHashPort passwordHash;
    private final CredentialContextPort credentialContext;

    public ChangePasswordUseCase(UserRepositoryPort userRepo,
                                 PasswordHashPort passwordHash,
                                 CredentialContextPort credentialContext,
                                 PermissionGuard guard) {
        super(guard);
        this.userRepo = userRepo;
        this.passwordHash = passwordHash;
        this.credentialContext = credentialContext;
    }

    @Override
    protected ActionPermissionPolicy permissionPolicy() { return POLICY; }

    /** Usuário altera sua própria senha (requer senha atual). */
    public void changeOwn(String currentPassword, String newPassword, String confirmPassword) {
        var principal = credentialContext.getAuthenticatedPrincipal()
                .orElseThrow(() -> new DomainAuthenticationException("unauthenticated"));
        run(PasswordAction.CHANGE_OWN_PASSWORD, () ->
                        doChange(principal.getUserId(), currentPassword, newPassword, confirmPassword, true),
                principal.getUserId(), "***");
    }

    /** Admin (ou quem tiver permissão) altera a senha de outro usuário (sem senha atual). */
    public void changeAny(Long targetUserId, String newPassword, String confirmPassword) {
        // ainda assim exige estar autenticado e ter USER_CHANGE_ANY_PASSWORD
        run(PasswordAction.CHANGE_ANY_PASSWORD, () ->
                        doChange(targetUserId, null, newPassword, confirmPassword, false),
                targetUserId, "***");
    }

    private void doChange(Long targetUserId,
                          String currentPassword,
                          String newPassword,
                          String confirmPassword,
                          boolean requireCurrent) {

        var violations = new ArrayList<DomainViolation>();
        if (targetUserId == null) {
            violations.add(new DomainViolation("userId","obrigatório","Required"));
        }
        if (newPassword == null || newPassword.length() < 6) {
            violations.add(new DomainViolation("newPassword","mínimo de 6 caracteres","Size"));
        }
        if (!Objects.equals(newPassword, confirmPassword)) {
            violations.add(new DomainViolation("confirmPassword","confirmação não confere","Confirm"));
        }
        if (requireCurrent && (currentPassword == null || currentPassword.isBlank())) {
            violations.add(new DomainViolation("currentPassword","obrigatória para troca própria","Required"));
        }
        if (!violations.isEmpty()) throw new DomainValidationException(violations);

        User u = userRepo.findById(targetUserId)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        if (requireCurrent) {
            if (u.getPasswordHash() == null || !passwordHash.matches(currentPassword, u.getPasswordHash())) {
                throw new DomainAuthenticationException("invalid_credentials"); // 401 ProblemDetail
            }
            // reforça autoria: mesmo com permissão OWN, só pode trocar a própria
            var principal = credentialContext.getAuthenticatedPrincipal()
                    .orElseThrow(() -> new DomainAuthenticationException("unauthenticated"));
            if (!Objects.equals(principal.getUserId(), targetUserId)) {
                throw new DomainAccessDeniedException(Permissions.USER_CHANGE_OWN_PASSWORD); // 403
            }
        }

        String newHash = passwordHash.encode(newPassword);
        u.setPasswordHash(newHash);
        userRepo.save(u);
    }
}
