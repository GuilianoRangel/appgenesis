package br.ueg.app.config;

import br.ueg.appgenesis.core.infrastructure.web.GenericDtoRestController;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Configuration
public class OpenApiConfig {

    public static final String SWAGGER_LICENSE_URL = "http://www.apache.org/licenses/LICENSE-2.0";
    public static final String SWAGGER_LICENSE = "Apache License 2.0";
    public static final String BEARER_AUTH = "bearerAuth";

    @Value("${app.api.swagger.title:TripRequest API}")
    private String title;

    @Bean
    public OpenAPI customOpenAPI(@Value("${app.api.version:1.0.0}") String appVersion) {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes(BEARER_AUTH,
                        new SecurityScheme()
                                .name(BEARER_AUTH)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                ))
                .info(new Info()
                        .title(this.title)
                        .version(appVersion)
                        .license(new License().name(SWAGGER_LICENSE).url(SWAGGER_LICENSE_URL))
                )
                .security(Arrays.asList(
                        new io.swagger.v3.oas.models.security.SecurityRequirement().addList(BEARER_AUTH)
                ));
    }

    /**
     * Personaliza o operationId: <nomeDoBeanCamelCase>_<nomeDoMetodo>
     * Ex.: departmentController_create
     */
    @Bean(name = "operationIdCustomizer")
    public OperationCustomizer operationIdCustomizer() {
        System.out.println("operationIdCustomizer");
        return new OperationCustomizer() {
            @Override
            public Operation customize(Operation operation, HandlerMethod handlerMethod) {
                Class<?> beanType = handlerMethod.getBeanType();
                Class<?> superClazz = beanType.getSuperclass();
                if (Objects.nonNull(superClazz) && GenericDtoRestController.class.isAssignableFrom(superClazz)) {
                    String beanName = beanType.getSimpleName();
                    beanName = beanName.substring(0, 1).toLowerCase() + beanName.substring(1);
                    operation.setOperationId(beanName + "_" + handlerMethod.getMethod().getName());
                }
                return operation;
            }
        };
    }

/*    @Bean
    public GroupedOpenApi departmentsGroup(
            @Qualifier("operationIdCustomizer") OperationCustomizer operationIdCustomizer) {
        return GroupedOpenApi.builder()
                .group("SecurityDepartments")
                .packagesToScan("com.triprequest.department.adapter.web")
                .pathsToMatch("/api/security/**")
                .addOperationCustomizer(operationIdCustomizer)
                .group("trip")
                .build();
    }*/

    @Bean
    public SmartInitializingSingleton openApiInitializer(
            ApplicationContext context,
            @Qualifier("operationIdCustomizer") OperationCustomizer operationIdCustomizer) {

        return () -> {
            List<String> controllerPackages = context.getBeansWithAnnotation(RestController.class)
                    .values().stream()
                    .map(controller -> controller.getClass().getPackage().getName())
                    .distinct()
                    .collect(Collectors.toList());

            controllerPackages.forEach(pkg -> {
                String simpleName = pkg.substring(pkg.lastIndexOf('.') + 1);
                GroupedOpenApi.builder()
                        .group("TripSecurity - " + simpleName)
                        .packagesToScan(pkg)
                        .pathsToMatch("/api/**")
                        .addOperationCustomizer(operationIdCustomizer)
                        .addOpenApiCustomizer(openApi ->
                                openApi.info(new Info()
                                        .title("TripRequest API - " + simpleName)
                                        .version("v1.0")
                                        .description("Documentação automática para os módulos de segurança e departamentos.")))
                        .build();
            });
        };
    }
}
