package br.ueg.appgenesis.security.usecase;

import br.ueg.appgenesis.core.port.discovery.SecuredServiceDiscoveryPort;
import br.ueg.appgenesis.core.security.ActionKey;
import br.ueg.appgenesis.core.security.ActionPermissionPolicy;
import br.ueg.appgenesis.core.usecase.ActionSecuredService;
import br.ueg.appgenesis.core.usecase.PermissionGuard;
import br.ueg.appgenesis.core.usecase.introspection.PermissionExtractionUtils;
import br.ueg.appgenesis.security.domain.Group;
import br.ueg.appgenesis.security.domain.Permission;
import br.ueg.appgenesis.security.port.GroupRepositoryPort;
import br.ueg.appgenesis.security.port.PermissionRepositoryPort;

import java.util.*;
import java.util.stream.Collectors;

public class PermissionSyncUseCase extends ActionSecuredService {

    public enum SyncAction implements ActionKey { SYNC }

    private static final String PERMISSION_SYNC = "PERMISSION_SYNC";

    private final SecuredServiceDiscoveryPort discoveryPort;
    private final PermissionRepositoryPort permissionRepo;
    private final GroupRepositoryPort groupRepo;
    private final MembershipService membershipService;

    public PermissionSyncUseCase(SecuredServiceDiscoveryPort discoveryPort,
                                 PermissionRepositoryPort permissionRepo,
                                 GroupRepositoryPort groupRepo,
                                 MembershipService membershipService,
                                 PermissionGuard guard) {
        super(guard);
        this.discoveryPort = discoveryPort;
        this.permissionRepo = permissionRepo;
        this.groupRepo = groupRepo;
        this.membershipService = membershipService;
    }

    @Override
    protected ActionPermissionPolicy permissionPolicy() {
        return ActionPermissionPolicy.builder()
                .action(SyncAction.SYNC, PERMISSION_SYNC)
                .build();
    }

    /** Filtra pacotes e garante permissões e vínculos ao grupo adminName. */
    public Result sync(List<String> basePackages, String adminName) {
        return execute(SyncAction.SYNC, () -> doSync(basePackages, adminName), basePackages, adminName);
    }

    private Result doSync(List<String> basePackages, String adminName) {
        Group admin = groupRepo.findAll().stream()
                .filter(g -> adminName.equalsIgnoreCase(g.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Grupo ADMIN não encontrado: " + adminName));

        var discovered = discoveryPort.discoverPolicies(basePackages);

        // coleta permissões
        Map<String, Set<String>> serviceToPerms = new LinkedHashMap<>();
        Set<String> allPerms = new TreeSet<>();
        for (SecuredServiceDiscoveryPort.DiscoveredPolicy dp : discovered) {
            var map = PermissionExtractionUtils.extractAll(dp.policy());
            var perms = map.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
            if (!perms.isEmpty()) {
                serviceToPerms.put(dp.ownerClassName(), perms);
                allPerms.addAll(perms);
            }
        }

        // upsert permissions
        Map<String, Permission> persisted = new HashMap<>();
        for (String code : allPerms) {
            Permission p = upsertPermission(code, "Permissão auto-registrada: " + code, "auto-scan");
            persisted.put(code, p);
        }

        // vincula ao ADMIN
        for (Permission p : persisted.values()) {
            membershipService.grantPermissionToGroup(admin.getId(), p.getId());
        }

        return new Result(admin.getName(), serviceToPerms, persisted.keySet());
    }

    private Permission upsertPermission(String code, String description, String scope) {
        return permissionRepo.findByCode(code).map(p -> {
            boolean changed = false;
            if (!Objects.equals(p.getDescription(), description)) { p.setDescription(description); changed = true; }
            if (!Objects.equals(p.getScope(), scope))             { p.setScope(scope);             changed = true; }
            return changed ? permissionRepo.save(p) : p;
        }).orElseGet(() -> permissionRepo.save(
                Permission.builder().code(code).description(description).scope(scope).build()
        ));
    }

    /* =========== Resultado resumido para retorno em API/Runner =========== */
    public record Result(String adminGroup,
                         Map<String, Set<String>> serviceToPermissions,
                         Set<String> allPermissions) {}
}
