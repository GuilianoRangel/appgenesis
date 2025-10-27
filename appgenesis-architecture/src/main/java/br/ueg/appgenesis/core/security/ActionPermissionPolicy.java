package br.ueg.appgenesis.core.security;

import java.util.*;

public class ActionPermissionPolicy {
    private final Map<String, List<String>> map = new HashMap<>();

    public ActionPermissionPolicy require(ActionKey action, List<String> permissions) {
        map.put(action.name(), permissions == null ? List.of() : List.copyOf(permissions));
        return this;
    }
    public ActionPermissionPolicy require(ActionKey action, String... permissions) {
        return require(action, permissions == null ? List.of() : Arrays.asList(permissions));
    }
    public List<String> get(ActionKey action) {
        return map.getOrDefault(action.name(), List.of());
    }

    public static Builder builder(){ return new Builder(); }
    public static class Builder {
        private final ActionPermissionPolicy p = new ActionPermissionPolicy();
        public Builder create(String... perms){ p.require(CrudAction.CREATE, perms); return this; }
        public Builder read(String... perms){ p.require(CrudAction.READ, perms); return this; }
        public Builder update(String... perms){ p.require(CrudAction.UPDATE, perms); return this; }
        public Builder patch(String... perms){ p.require(CrudAction.PATCH, perms); return this; }
        public Builder delete(String... perms){ p.require(CrudAction.DELETE, perms); return this; }
        public Builder list(String... perms){ p.require(CrudAction.LIST, perms); return this; }
        /** Ações extras (custom) */
        public Builder action(ActionKey action, String... perms){ p.require(action, perms); return this; }
        public ActionPermissionPolicy build(){ return p; }
    }
}
