package br.ueg.appgenesis.security.bootstrap;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "app.permission.scan")
public class PermissionsScanProperties {
    /** Pacotes a filtrar (prefixos). Ex.: ["com.triprequest.security.usecase","com.core.usecase"] */
    private List<String> basePackages = List.of();
    /** Nome do grupo admin. */
    private String adminGroupName = "ADMIN";
}
