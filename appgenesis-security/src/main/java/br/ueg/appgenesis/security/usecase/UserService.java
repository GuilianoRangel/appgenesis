package br.ueg.appgenesis.security.usecase;

import br.ueg.appgenesis.core.domain.validation.DomainValidationException;
import br.ueg.appgenesis.core.domain.validation.DomainViolation;
import br.ueg.appgenesis.core.security.ActionKey;
import br.ueg.appgenesis.core.security.ActionPermissionPolicy;
import br.ueg.appgenesis.core.security.CrudAction;
import br.ueg.appgenesis.core.usecase.GenericCrudService;
import br.ueg.appgenesis.core.usecase.PermissionGuard;
import br.ueg.appgenesis.core.usecase.support.AuditableInitializer;
import br.ueg.appgenesis.core.usecase.support.FieldMergeService;
import br.ueg.appgenesis.security.domain.User;
import br.ueg.appgenesis.security.port.DepartmentRepositoryPort;
import br.ueg.appgenesis.security.port.UserRepositoryPort;

import java.util.ArrayList;
import java.util.function.Supplier;

public class UserService extends GenericCrudService<User, Long> {

    private final DepartmentRepositoryPort repoDepartment;
    private final UserRepositoryPort repoUser;

    // ação customizada
    public enum UserActions implements ActionKey { ARCHIVE, RESTORE }

    private static final ActionPermissionPolicy POLICY = ActionPermissionPolicy.builder()
            .create("USER_WRITE")
            .update("USER_WRITE")
            .patch("USER_WRITE")
            .delete("USER_WRITE")
            .read("USER_READ")
            .list("USER_READ")
            .action(UserActions.ARCHIVE, "USER_WRITE")
            .action(UserActions.RESTORE, "USER_WRITE")
            .build();

    public UserService(UserRepositoryPort repo,
                       DepartmentRepositoryPort repoDepartment,
                       AuditableInitializer auditable,
                       FieldMergeService merge,
                       PermissionGuard guard) {
        super(repo,  auditable, merge, guard); // guard opcional (pode ser null)
        this.repoUser = repo;
        this.repoDepartment = repoDepartment;
    }

    @Override protected ActionPermissionPolicy permissionPolicy() { return POLICY; }

    @Override
    protected void before(ActionKey action, Object... args) {
        if(
                (action.equals(CrudAction.CREATE) || action.equals(CrudAction.UPDATE))
                && args[0] !=null && args[0] instanceof User
        ) {
            var user =  (User)args[0];
            var errors = new ArrayList<DomainViolation>();
            repoDepartment.findById(user.getDepartmentId()).orElseGet(() -> {
                errors.add(new DomainViolation("departmentId", "departamento inexistente (id:"+user.getDepartmentId()+")", "Exists"));
                return null;
            });
            //TODO: tratar apenas para Creação, para update e diferente.
            if (this.repoUser.findByUsername(user.getUsername()).isPresent()){
                errors.add(new DomainViolation("userName", "Nome de Usuário já existe (username: "+user.getUsername()+")", "Exists"));
            }
            if(this.repoUser.existsByEmail(user.getEmail())){
                errors.add(new DomainViolation("email", "Email já existe (username: "+user.getUsername()+")", "Exists"));
            }
            if (!errors.isEmpty()) throw new DomainValidationException(errors);
        }
    }

    public User archive(Long id) {
        return execute(UserActions.ARCHIVE, doChangeStatus(id, "INACTIVE"));
    }

    private Supplier<User> doChangeStatus(Long id, String INACTIVE) {
        return () -> {
            User u = find(id); // usa READ (já checado)
            u.setStatus(INACTIVE);
            return repository.save(u);
        };
    }

    public User restore(Long id) {
        return execute(UserActions.RESTORE, doChangeStatus(id, "ACTIVE"));
    }
}
