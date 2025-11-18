package br.ueg.appgenesis.security.bootstrap;

import br.ueg.appgenesis.core.security.CredentialContextPort;
import br.ueg.appgenesis.security.domain.Department;
import br.ueg.appgenesis.security.domain.Group;
import br.ueg.appgenesis.security.domain.Permission;
import br.ueg.appgenesis.security.domain.User;
import br.ueg.appgenesis.security.port.GroupRepositoryPort;
import br.ueg.appgenesis.security.port.PermissionRepositoryPort;
import br.ueg.appgenesis.security.port.UserRepositoryPort;
import br.ueg.appgenesis.security.port.auth.PasswordHashPort;
import br.ueg.appgenesis.security.usecase.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
@RequiredArgsConstructor
@Order(0)
public class SecuritySeedRunner implements ApplicationRunner {

    public static final String USER_CHANGE_OWN_PASSWORD = "USER_CHANGE_OWN_PASSWORD";
    public static final String USER_CHANGE_ANY_PASSWORD = "USER_CHANGE_ANY_PASSWORD";
    private final DepartmentService departmentService;
    private final UserService userService;
    private final GroupService groupService;
    private final PermissionService permissionService;
    private final MembershipService membershipService;

    private final CredentialContextPort credentialContext;

    // Ports com finders específicos (quando disponíveis)
    private final UserRepositoryPort userRepo;
    private final PermissionRepositoryPort permissionRepo;
    private final GroupRepositoryPort groupRepo;

    private final PasswordHashPort passwordHashPort; // <--- AQUI

    // Códigos de permissão padronizados (alinhados aos use cases CRUD/links)
    private static final String DEPARTMENT_READ   = "DEPARTMENT_READ";
    private static final String DEPARTMENT_WRITE  = "DEPARTMENT_WRITE";
    private static final String USER_READ         = "USER_READ";
    private static final String USER_WRITE        = "USER_WRITE";
    private static final String GROUP_READ        = "GROUP_READ";
    private static final String GROUP_WRITE       = "GROUP_WRITE";
    private static final String PERMISSION_READ   = "PERMISSION_READ";
    private static final String PERMISSION_WRITE  = "PERMISSION_WRITE";
    // Para operações de vinculação (MembershipController / MembershipService)
    private static final String MEMBERSHIP_MANAGE = "MEMBERSHIP_MANAGE";

    @Override
    public void run(ApplicationArguments args) {

        // registra principal “SEED” com todas as permissões necessárias
        var seedPrincipal = new AuthenticationService.SimplePrincipal(
                0L, "SEED",
                java.util.List.of(
                        DEPARTMENT_READ, DEPARTMENT_WRITE,
                        USER_READ, USER_WRITE,
                        GROUP_READ, GROUP_WRITE,
                        PERMISSION_READ, PERMISSION_WRITE,
                        MEMBERSHIP_MANAGE
                )
        );

        credentialContext.setAuthenticatedPrincipal(seedPrincipal);
        try {


            // 1) Departments
            Department eng = upsertDepartment("Engineering", "Desenvolvimento e engenharia", "ACTIVE");
            Department hr  = upsertDepartment("HR",          "Recursos Humanos",            "ACTIVE");

            // gera hash via port
            String adminHash = passwordHashPort.encode("123456");

            User admin = upsertUser("admin", "Administrator", "admin@example.com",
                    eng.getId(), "ACTIVE", adminHash);
            User alice = upsertUser("alice", "Alice Silva", "alice@example.com",
                    eng.getId(), "ACTIVE", null);
            User bob   = upsertUser("bob", "Bob Souza", "bob@example.com",
                    hr.getId(),  "ACTIVE", null);

            if (eng.getManagerId() == null || !eng.getManagerId().equals(alice.getId())) {
                eng.setManagerId(alice.getId());
                departmentService.patch(eng.getId(), eng);
            }

            // 3) Groups
            Group adminGroup = upsertGroup("ADMIN", "Administradores do sistema", "ACTIVE");
            Group userGroup  = upsertGroup("USER",  "Usuários padrão",            "ACTIVE");

            Permission pDeptRead   = upsertPermission(DEPARTMENT_READ,          "Pode ler departamentos",               "security");
            Permission pDeptWrite  = upsertPermission(DEPARTMENT_WRITE,         "Pode criar/editar departamentos",      "security");
            Permission pUserRead   = upsertPermission(USER_READ,                "Pode ler usuários",                    "security");
            Permission pUserWrite  = upsertPermission(USER_WRITE,               "Pode criar/editar usuários",           "security");
            Permission pGroupRead  = upsertPermission(GROUP_READ,               "Pode ler grupos",                      "security");
            Permission pGroupWrite = upsertPermission(GROUP_WRITE,              "Pode criar/editar grupos",             "security");
            Permission pPermRead   = upsertPermission(PERMISSION_READ,          "Pode ler permissões",                  "security");
            Permission pPermWrite  = upsertPermission(PERMISSION_WRITE,         "Pode criar/editar permissões",         "security");
            Permission pMemberMng  = upsertPermission(MEMBERSHIP_MANAGE,        "Pode gerenciar vínculos e permissões", "security");
            Permission pOwn        = upsertPermission(USER_CHANGE_OWN_PASSWORD, "Trocar a própria senha",               "security");
            Permission pAny        = upsertPermission(USER_CHANGE_ANY_PASSWORD, "Trocar a senha de qualquer usuário",   "security");


            // 5) Links (idempotentes)
            // Usuários -> Grupos
            membershipService.assignUserToGroup(admin.getId(), adminGroup.getId());
            membershipService.assignUserToGroup(alice.getId(), adminGroup.getId());
            membershipService.assignUserToGroup(bob.getId(),   userGroup.getId());

            // ADMIN recebe todas as permissões
            grantAllAdmin(adminGroup,
                    pDeptRead, pDeptWrite,
                    pUserRead, pUserWrite,
                    pGroupRead, pGroupWrite,
                    pPermRead, pPermWrite,
                    pMemberMng, pOwn, pAny
            );

            // USER recebe apenas leitura básica
            membershipService.grantPermissionToGroup(userGroup.getId(), pDeptRead.getId());
            membershipService.grantPermissionToGroup(userGroup.getId(), pUserRead.getId());
            membershipService.grantPermissionToGroup(userGroup.getId(), pGroupRead.getId());
            membershipService.grantPermissionToGroup(userGroup.getId(), pPermRead.getId());
            membershipService.grantPermissionToGroup(userGroup.getId(), pOwn.getId());
        }finally {
            // limpa o contexto para não “vazar” após o seed
            credentialContext.clear();
        }
    }

    /* ========================= UPSERTS POR PORT/SERVICE ========================= */

    private Department upsertDepartment(String name, String description, String status) {
        // Sem finder específico: busca por nome via listAll e filtra
        Optional<Department> found = departmentService.findAll().stream()
                .filter(d -> name.equalsIgnoreCase(d.getName()))
                .findFirst();
        if (found.isPresent()) {
            Department d = found.get();
            boolean changed = false;
            if (!equalsStr(d.getDescription(), description)) { d.setDescription(description); changed = true; }
            if (!equalsStr(d.getStatus(), status))           { d.setStatus(status);           changed = true; }
            return changed ? departmentService.patch(d.getId(), d) : d;
        }
        Department input = Department.builder()
                .name(name).description(description).status(status).build();
        return departmentService.create(input);
    }

    /**
     * Upsert de usuário, com opção de atualizar o passwordHash se fornecido.
     * Se {@code passwordHash} for null, a senha não é alterada (preserva a atual).
     */
    private User upsertUser(String username, String fullName, String email,
                            Long departmentId, String status, String passwordHash) {

        Optional<User> byUsername = userRepo.findByUsername(username);
        if (byUsername.isPresent()) {
            User u = byUsername.get();
            boolean changed = false;
            if (!equalsStr(u.getFullName(), fullName))      { u.setFullName(fullName);           changed = true; }
            if (!equalsStr(u.getEmail(), email))            { u.setEmail(email);                  changed = true; }
            if (!equalsObj(u.getDepartmentId(), departmentId)) { u.setDepartmentId(departmentId); changed = true; }
            if (!equalsStr(u.getStatus(), status))          { u.setStatus(status);                changed = true; }
            if (passwordHash != null && !equalsStr(u.getPasswordHash(), passwordHash)) {
                u.setPasswordHash(passwordHash); // campo interno: permitido no create/update aqui pela seed
                changed = true;
            }
            return changed ? userService.patch(u.getId(), u) : u;
        }

        // Evita colisão de e-mail
        if (userRepo.existsByEmail(email)) {
            // se já existir por e-mail mas não por username, carrega por e-mail via listAll
            Optional<User> byEmail = userService.findAll().stream()
                    .filter(u -> email.equalsIgnoreCase(u.getEmail()))
                    .findFirst();
            if (byEmail.isPresent()) {
                User u = byEmail.get();
                boolean changed = false;
                if (!equalsStr(u.getUsername(), username))   { u.setUsername(username); changed = true; }
                if (!equalsStr(u.getFullName(), fullName))   { u.setFullName(fullName); changed = true; }
                if (!equalsObj(u.getDepartmentId(), departmentId)) { u.setDepartmentId(departmentId); changed = true; }
                if (!equalsStr(u.getStatus(), status))       { u.setStatus(status);     changed = true; }
                if (passwordHash != null && !equalsStr(u.getPasswordHash(), passwordHash)) {
                    u.setPasswordHash(passwordHash);
                    changed = true;
                }
                return changed ? userService.patch(u.getId(), u) : u;
            }
        }

        User input = User.builder()
                .username(username).fullName(fullName).email(email)
                .departmentId(departmentId).status(status)
                .passwordHash(passwordHash) // define a senha no create (se fornecida)
                .build();
        return userService.create(input);
    }

    private Group upsertGroup(String name, String description, String status) {
        // Sem finder específico: usa service.listAll
        Optional<Group> found = groupService.findAll().stream()
                .filter(g -> name.equalsIgnoreCase(g.getName()))
                .findFirst();
        if (found.isPresent()) {
            Group g = found.get();
            boolean changed = false;
            if (!equalsStr(g.getDescription(), description)) { g.setDescription(description); changed = true; }
            if (!equalsStr(g.getStatus(), status))           { g.setStatus(status);           changed = true; }
            return changed ? groupService.patch(g.getId(), g) : g;
        }
        Group input = Group.builder()
                .name(name).description(description).status(status).build();
        return groupService.create(input);
    }

    private Permission upsertPermission(String code, String description, String scope) {
        Optional<Permission> found = permissionRepo.findByCode(code);
        if (found.isPresent()) {
            Permission p = found.get();
            boolean changed = false;
            if (!equalsStr(p.getDescription(), description)) { p.setDescription(description); changed = true; }
            if (!equalsStr(p.getScope(), scope))             { p.setScope(scope);             changed = true; }
            return changed ? permissionService.patch(p.getId(), p) : p;
        }
        Permission input = Permission.builder()
                .code(code).description(description).scope(scope).build();
        return permissionService.create(input);
    }

    private void grantAllAdmin(Group adminGroup, Permission... perms) {
        for (Permission p : perms) {
            membershipService.grantPermissionToGroup(adminGroup.getId(), p.getId());
        }
    }

    private boolean equalsStr(String a, String b) {
        return (a == null && b == null) || (a != null && a.equals(b));
    }
    private boolean equalsObj(Object a, Object b) {
        return (a == null && b == null) || (a != null && a.equals(b));
    }
}
