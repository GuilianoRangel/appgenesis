package br.ueg.appgenesis.security.adapter.discovery;

import br.ueg.appgenesis.core.port.discovery.SecuredServiceDiscoveryPort;
import br.ueg.appgenesis.core.security.ActionPermissionPolicy;
import br.ueg.appgenesis.core.usecase.ActionSecuredService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;

@Component
@RequiredArgsConstructor
public class SpringSecuredServiceDiscoveryAdapter implements SecuredServiceDiscoveryPort {

    private final ApplicationContext ctx;

    @Override
    public List<DiscoveredPolicy> discoverPolicies(List<String> basePackagePrefixes) {
        Map<String, ActionSecuredService> beans = ctx.getBeansOfType(ActionSecuredService.class);
        if (beans.isEmpty()) return List.of();

        List<DiscoveredPolicy> out = new ArrayList<>();
        for (ActionSecuredService bean : beans.values()) {
            String className = bean.getClass().getName();
            if (!startsWithAny(className, basePackagePrefixes)) continue;

            ActionPermissionPolicy policy = invokePolicy(bean);
            if (policy != null) out.add(new DiscoveredPolicy(className, policy));
        }
        return out;
    }

    private boolean startsWithAny(String fqcn, List<String> prefixes) {
        if (prefixes == null || prefixes.isEmpty()) return true; // sem filtro = todos
        for (String p : prefixes) {
            if (fqcn.startsWith(p)) return true;
        }
        return false;
    }

    private ActionPermissionPolicy invokePolicy(Object service) {
        try {
            Method m;
            try {
                m = service.getClass().getDeclaredMethod("permissionPolicy");
            } catch (NoSuchMethodException e) {
                m = service.getClass().getSuperclass().getDeclaredMethod("permissionPolicy");
            }
            m.setAccessible(true);
            Object r = m.invoke(service);
            return (ActionPermissionPolicy) r;
        } catch (Exception e) {
            // Se algum service não expõe policy, apenas ignore
            return null;
        }
    }
}
