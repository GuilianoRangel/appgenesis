package br.ueg.appgenesis.security.config;

import br.ueg.appgenesis.core.port.discovery.SecuredServiceDiscoveryPort;
import br.ueg.appgenesis.core.usecase.PermissionGuard;
import br.ueg.appgenesis.security.port.GroupRepositoryPort;
import br.ueg.appgenesis.security.port.PermissionRepositoryPort;
import br.ueg.appgenesis.security.usecase.MembershipService;
import br.ueg.appgenesis.security.usecase.PermissionSyncUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PermissionSyncConfig {

    @Bean
    public PermissionSyncUseCase permissionSyncUseCase(SecuredServiceDiscoveryPort discoveryPort,
                                                       PermissionRepositoryPort permissionRepo,
                                                       GroupRepositoryPort groupRepo,
                                                       MembershipService membershipService,
                                                       PermissionGuard guard // pode ser null num @Bean alternativo
    ) {
        return new PermissionSyncUseCase(discoveryPort, permissionRepo, groupRepo, membershipService, guard);
    }
}
