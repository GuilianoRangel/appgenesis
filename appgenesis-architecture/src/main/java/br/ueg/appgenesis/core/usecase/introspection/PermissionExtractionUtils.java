package br.ueg.appgenesis.core.usecase.introspection;

import br.ueg.appgenesis.core.security.ActionKey;
import br.ueg.appgenesis.core.security.ActionPermissionPolicy;

import java.lang.reflect.Field;
import java.util.*;

public final class PermissionExtractionUtils {
    private PermissionExtractionUtils() {}
    @SuppressWarnings("unchecked")
    public static Map<ActionKey, List<String>> extractAll(ActionPermissionPolicy policy) {
        if (policy == null) return Map.of();
        try {
            var m = policy.getClass().getMethod("toMap");
            Object r = m.invoke(policy);
            if (r instanceof Map) return (Map<ActionKey, List<String>>) r;
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) { throw new IllegalStateException("Falha ao invocar policy.toMap()", e); }
        try {
            Field f = policy.getClass().getDeclaredField("map");
            f.setAccessible(true);
            Object r = f.get(policy);
            if (r instanceof Map) return (Map<ActionKey, List<String>>) r;
        } catch (Exception e) { throw new IllegalStateException("Não foi possível extrair permissões", e); }
        return Map.of();
    }
}
