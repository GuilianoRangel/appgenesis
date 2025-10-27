package br.ueg.appgenesis.core.usecase;

import br.ueg.appgenesis.core.security.ActionKey;
import br.ueg.appgenesis.core.security.ActionPermissionPolicy;

import java.util.List;
import java.util.function.Supplier;

/** Base para qualquer serviço orientado a ações com checagem de permissões. */
public abstract class ActionSecuredService {

    /** Opcional: se null, não há checagem. */
    protected final PermissionGuard permissionGuard;

    protected ActionSecuredService(PermissionGuard permissionGuard) {
        this.permissionGuard = permissionGuard;
    }

    protected ActionSecuredService() {
        this(null);
    }

    /** Subclasses podem sobrescrever para declarar permissões por ação. */
    protected ActionPermissionPolicy permissionPolicy() { return null; }

    /** Hooks para cross-cutting (auditoria, eventos…) */
    protected void before(ActionKey action, Object... args) {}
    protected void after(ActionKey action, Object result, Object... args) {}

    /* ===================== Template execution ===================== */

    protected void check(ActionKey action) {
        if (permissionGuard == null) return;
        ActionPermissionPolicy p = permissionPolicy();
        if (p == null) return;
        List<String> perms = p.get(action);
        if (!perms.isEmpty()) {
            permissionGuard.requirePermissions(perms.toArray(String[]::new));
        }
    }

    protected <R> R execute(ActionKey action, Supplier<R> block, Object... args) {
        check(action); before(action, args);
        R out = block.get();
        after(action, out, args);
        return out;
    }

    protected void run(ActionKey action, Runnable block, Object... args) {
        check(action); before(action, args);
        block.run(); after(action, null, args);
    }
}
