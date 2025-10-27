package br.ueg.appgenesis.security.bootstrap;

import br.ueg.appgenesis.core.security.CredentialContextPort;
import br.ueg.appgenesis.core.security.CredentialPrincipal;
import br.ueg.appgenesis.security.usecase.PermissionSyncUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.permissions.scan.enabled", havingValue = "true", matchIfMissing = true)
public class PermissionScannerRunner implements ApplicationRunner {

    private final PermissionSyncUseCase useCase;
    private final PermissionsScanProperties props;
    private final CredentialContextPort credentialContext; // <-- injete

    // Permissões mínimas para o sync funcionar de ponta a ponta
    private static final List<String> SEED_PERMS = List.of(
            "PERMISSION_SYNC",      // exigida pelo PermissionSyncUseCase
            "MEMBERSHIP_MANAGE"     // para vincular permissões ao grupo ADMIN
    );

    @Override
    public void run(ApplicationArguments args) {
        // principal temporário de SEED
        CredentialPrincipal seed = new CredentialPrincipal() {
            @Override
            public Long getUserId() {
                return 0L;
            }

            @Override
            public String getUsername() {
                return "SEED";
            }

            @Override
            public List<String> getPermissions() {
                return SEED_PERMS;
            }
        };

        credentialContext.setAuthenticatedPrincipal(seed);
        try {
            var result = useCase.sync(props.getBasePackages(), props.getAdminGroupName());
            log.info("[perm-scan] Grupo admin: {}", result.adminGroup());
            log.info("[perm-scan] Permissões total: {}", result.allPermissions().size());
            result.serviceToPermissions().forEach((svc, perms) ->
                    log.info("[perm-scan] {} -> {}", svc, String.join(", ", perms))
            );
        } finally {
            credentialContext.clear(); // não deixar “vazar” o principal
        }
    }
}
