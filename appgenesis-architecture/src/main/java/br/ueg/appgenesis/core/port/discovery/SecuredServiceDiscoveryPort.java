package br.ueg.appgenesis.core.port.discovery;

import br.ueg.appgenesis.core.security.ActionPermissionPolicy;
import java.util.List;

public interface SecuredServiceDiscoveryPort {

    /** Representa uma policy encontrada em um service seguro. */
    record DiscoveredPolicy(String ownerClassName, ActionPermissionPolicy policy) {}

    /**
     * Descobre policies de serviços que estendem ActionSecuredService,
     * filtrando por prefixos de pacote (ownerClassName startsWith de algum prefixo).
     */
    List<DiscoveredPolicy> discoverPolicies(List<String> basePackagePrefixes);
}
