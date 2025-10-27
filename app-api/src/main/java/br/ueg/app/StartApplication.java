package br.ueg.app;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "br.ueg.app.*",
        "br.ueg.appgenesis.core.*",
        "br.ueg.appgenesis.security.*",
        "com.triprequest.base.*"
}, exclude = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
@EntityScan(
        basePackageClasses = { Jsr310JpaConverters.class },
        basePackages = {
                "com.triprequest.base.adapter.persistence.*",
                //Para funcionamento da Arquitetura
                "br.ueg.appgenesis.security.adapter.persistence.*"
        }
)
@EnableJpaRepositories(
        basePackages = {
            "com.triprequest.base.adapter.persistence.*",
            //Para funcionamento da Arquitetura
            "br.ueg.appgenesis.security.adapter.persistence.*"
        }
)
@OpenAPIDefinition(servers = {@Server(url = "${server.url}", description = "Default Server URL")})
public class StartApplication {
    public static void main(String[] args) {
        SpringApplication.run(StartApplication.class, args);
    }
}
