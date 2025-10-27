Este roteiro é um **guia passo a passo para desenvolvedores juniores** para criar um **novo módulo** em um projeto existente, seguindo uma arquitetura de pacotes **separados** (domínio 100% agnóstico e infra/adapters em módulos distintos).

O objetivo é que você crie um novo módulo principal para o seu projeto (ex: `[SEU_MODULO_BASE]`) com dois submódulos:

*   `[SEU_MODULO_BASE]-domain` → **DOMÍNIO** (modelos, ports, use cases, regras de negócio, sem dependências de frameworks como Spring/JPA/Web).
*   `[SEU_MODULO_BASE]-infra` → **ADAPTERS/INFRA** (implementações de persistência com JPA, mappers com MapStruct, Controllers REST, configurações Spring, Flyway para migrações de banco de dados).

O caso de uso de exemplo neste roteiro é "Trip Category" (CRUD com permissões), mas você deverá adaptá-lo para o seu próprio caso de uso (ex: "Product", "Order", etc.), utilizando os placeholders `[SEU_MODULO_BASE]`, `[SEU_CASO_DE_USO]`, `[SUFIXO_PACOTE]`, etc.

---

## 0) Pré-requisitos e Contexto

Para iniciar, é fundamental entender a estrutura do projeto e garantir que o ambiente de desenvolvimento esteja configurado.

### 0.1) Estrutura Base do Projeto

O projeto segue uma arquitetura modular com os seguintes módulos principais:

*   `app-api`: Módulo principal da aplicação, responsável por orquestrar os demais módulos e expor a API.
*   `appgenesis-architecture`: Contém o core da arquitetura, com classes e interfaces genéricas para domínio, portas e infraestrutura (ex: `BaseEntity`, `GenericCrudService`, `ProblemDetail` handler).
*   `appgenesis-security`: Módulo de segurança, incluindo autenticação, autorização e gerenciamento de permissões (ex: `PermissionGuard`, `ActionSecuredService`).
*   `triprequest-base`: Um módulo de exemplo que demonstra a implementação de um caso de uso (`Trip Category`) seguindo a arquitetura proposta, com seus submódulos `triprequest-base-domain` e `triprequest-base-infra`.

### 0.2) Objetivo da Criação de Novos Módulos

O objetivo deste roteiro é guiar você na criação de **novos módulos** para o seu caso de uso específico, replicando a estrutura de `triprequest-base` e seus submódulos. Isso garante a separação de responsabilidades e a agnósticidade do domínio.

### 0.3) Pré-requisitos Rápidos

Para iniciar, o ambiente de desenvolvimento deve estar configurado com os seguintes itens já funcionando na base do projeto:

*   O projeto base já compila e roda (utilizando a base `appgenesis_v12_base_v1.zip` ou equivalente).
*   Você já tem no core (módulos `appgenesis-architecture` e `appgenesis-security`): `BaseEntity`, `ActionSecuredService`, `GenericCrudService`, `PermissionGuard`, validações de domínio (`@NotBlankD`, `@SizeD`…), `ProblemDetail` handler, mappers genéricos, etc.
*   Swagger já habilitado.
*   Permission scanner/seed já funcionando (será necessário apenas apontar para o novo pacote do seu caso de uso).

---

## 1) Criar os Módulos (Estrutura de Pastas e `pom.xml`)

### 1.1. Atualize o `pom.xml` do Agregador (na raiz do projeto)

Adicione os novos submódulos à seção `<modules>` do `pom.xml` principal. Lembre-se de substituir `[SEU_MODULO_BASE]` pelo nome do seu módulo principal (ex: `meuprojeto-base`).

```xml
<modules>
  <!-- ... outros módulos ... -->
  <module>[SEU_MODULO_BASE]/[SEU_MODULO_BASE]-domain</module>
  <module>[SEU_MODULO_BASE]/[SEU_MODULO_BASE]-infra</module>
</modules>
```

### 1.2. Crie a Pasta `[SEU_MODULO_BASE]` com Dois Submódulos

Crie a estrutura de pastas para o seu novo módulo principal e seus submódulos. Por exemplo, se `[SEU_MODULO_BASE]` for `meuprojeto-base`, você criaria:

```
[SEU_MODULO_BASE]/
├── [SEU_MODULO_BASE]-domain/
└── [SEU_MODULO_BASE]-infra/
```

Dentro de cada um desses submódulos, você criará seus respectivos arquivos `pom.xml`.

### 1.3. `[SEU_MODULO_BASE]-domain/pom.xml` (Módulo de Domínio)

Este `pom.xml` define o módulo de domínio, que será 100% agnóstico a frameworks. Substitua `[SEU_MODULO_BASE]` pelo nome do seu módulo principal.

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>br.ueg.appgenesis</groupId>
        <artifactId>triprequest</artifactId>
        <version>1.0.0</version>
    </parent>

    <artifactId>[SEU_MODULO_BASE]-domain</artifactId>
    <name>TripRequest :: [SEU_MODULO_BASE] Domain Module</name>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <properties>
        <java.version>17</java.version>
        <mapstruct.version>1.5.5.Final</mapstruct.version>
        <!-- Defina a versão do seu módulo base aqui, se necessário -->
        <seu.modulo.base.version>1.0.0</seu.modulo.base.version>
    </properties>

    <dependencies>
        <!-- Depende apenas do core de arquitetura (sem spring!) -->
        <dependency>
            <groupId>br.ueg.appgenesis</groupId>
            <artifactId>appgenesis-architecture</artifactId>
            <version>${project.version}</version>
        </dependency>
        <!-- Lombok para getters/setters/builders -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>
        <!-- (Opcional) junit para testes de domínio -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-api</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-engine</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>${java.version}</source>
                    <target>${java.version}</target>
                    <parameters>true</parameters>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                            <version>${lombok.version}</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### 1.4. `[SEU_MODULO_BASE]-infra/pom.xml` (Módulo de Infraestrutura/Adapters)

Este `pom.xml` define o módulo de infraestrutura, que conterá as implementações específicas de frameworks. Substitua `[SEU_MODULO_BASE]` pelo nome do seu módulo principal.

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.triprequest</groupId>
        <artifactId>triprequest</artifactId>
        <version>1.0.0</version>
    </parent>

    <artifactId>[SEU_MODULO_BASE]-infra</artifactId>
    <name>TripRequest :: [SEU_MODULO_BASE] Infra Module</name>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <properties>
        <java.version>17</java.version>
        <mapstruct.version>1.5.5.Final</mapstruct.version>
        <!-- Defina a versão do seu módulo base aqui -->
        <seu.modulo.base.version>1.0.0</seu.modulo.base.version>
    </properties>

    <dependencies>
        <!-- Dependência do módulo de domínio -->
        <dependency>
            <groupId>com.triprequest</groupId>
            <artifactId>[SEU_MODULO_BASE]-domain</artifactId>
            <version>${seu.modulo.base.version}</version>
        </dependency>

        <!-- Dependência do módulo de segurança -->
        <dependency>
            <groupId>br.ueg.appgenesis</groupId>
            <artifactId>appgenesis-security</artifactId>
            <version>${project.version}</version>
        </dependency>

        <!-- Spring Boot Web + JPA + Validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- MapStruct + Lombok -->
        <dependency>
            <groupId>org.mapstruct</groupId>
            <artifactId>mapstruct</artifactId>
            <version>${mapstruct.version}</version>
        </dependency>
        <dependency>
            <groupId>org.mapstruct</groupId>
            <artifactId>mapstruct-processor</artifactId>
            <version>${mapstruct.version}</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>

        <!-- OpenAPI UI -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>2.6.0</version>
        </dependency>

        <!-- Postgres driver (se aplicável) -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
        </dependency>

        <!-- Testes -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-api</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-engine</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- Annotation Processing p/ MapStruct e Lombok -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>${java.version}</source>
                    <target>${java.version}</target>
                    <parameters>true</parameters>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                            <version>${lombok.version}</version>
                        </path>
                        <path>
                            <groupId>org.mapstruct</groupId>
                            <artifactId>mapstruct-processor</artifactId>
                            <version>${mapstruct.version}</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## 2) Domínio (100% Agnóstico)

Esta seção descreve a criação dos componentes de domínio, que devem ser 100% agnósticos a frameworks.

**Pacote raiz do domínio:** `com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE]` (dentro de `[SEU_MODULO_BASE]-domain`).

### 2.1. Modelo de Domínio

Crie a classe de domínio `domain/[SEU_CASO_DE_USO].java`. Substitua `[SEU_CASO_DE_USO]` pelo nome do seu caso de uso (ex: `Product`, `Order`).

```java
package com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].domain;

import com.core.domain.BaseEntity;
import com.core.domain.validation.DomainNotBlank;
import com.core.domain.validation.DomainSize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class [SEU_CASO_DE_USO] extends BaseEntity<Long> {

    @DomainNotBlank
    @DomainSize(min = 2, max = 30)
    private String code; // Exemplo de campo

    @DomainNotBlank
    @DomainSize(min = 3, max = 100)
    private String name; // Exemplo de campo

    @DomainSize(max = 255)
    private String description; // Exemplo de campo

    private Boolean active; // Exemplo de campo, default será aplicado no use case create
}
```

### 2.2. Port de Repositório

Crie a interface `port/[SEU_CASO_DE_USO]RepositoryPort.java`. Este port define o contrato para a persistência do seu domínio, sem detalhes de implementação.

```java
package com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].port;

import com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].domain.[SEU_CASO_DE_USO];
import java.util.List;
import java.util.Optional;

public interface [SEU_CASO_DE_USO]RepositoryPort {
    [SEU_CASO_DE_USO] save([SEU_CASO_DE_USO] domain);
    Optional<[SEU_CASO_DE_USO]> findById(Long id);
    List<[SEU_CASO_DE_USO]> findAll();
    void deleteById(Long id);

    // Exemplo de métodos de busca específicos
    boolean existsByCode(String code);
    Optional<[SEU_CASO_DE_USO]> findByCode(String code);
}
```

### 2.3. Use Case (CRUD com Guard de Permissões)

Crie a classe `usecase/[SEU_CASO_DE_USO]Service.java`. Este use case implementa a lógica de negócio e utiliza o `PermissionGuard` para controle de acesso.

```java
package com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].usecase;

import com.core.security.ActionPermissionPolicy;
import com.core.security.CrudAction;
import com.core.usecase.AuditableInitializer;
import com.core.usecase.FieldMergeService;
import com.core.usecase.GenericCrudService;
import com.core.usecase.PermissionGuard;
import com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].domain.[SEU_CASO_DE_USO];
import com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].port.[SEU_CASO_DE_USO]RepositoryPort;

public class [SEU_CASO_DE_USO]Service extends GenericCrudService<[SEU_CASO_DE_USO], Long> {

    public static final class [SEU_CASO_DE_USO]Permissions {
        public static final String [SEU_CASO_DE_USO_UPPER]_READ = "[SEU_CASO_DE_USO_UPPER]_READ";
        public static final String [SEU_CASO_DE_USO_UPPER]_WRITE = "[SEU_CASO_DE_USO_UPPER]_WRITE";

        private [SEU_CASO_DE_USO]Permissions() {
        }
    }

    private static final ActionPermissionPolicy POLICY = ActionPermissionPolicy.builder()
            .action(CrudAction.READ, [SEU_CASO_DE_USO]Permissions.[SEU_CASO_DE_USO_UPPER]_READ)
            .action(CrudAction.LIST, [SEU_CASO_DE_USO]Permissions.[SEU_CASO_DE_USO_UPPER]_READ)
            .action(CrudAction.CREATE, [SEU_CASO_DE_USO]Permissions.[SEU_CASO_DE_USO_UPPER]_WRITE)
            .action(CrudAction.UPDATE, [SEU_CASO_DE_USO]Permissions.[SEU_CASO_DE_USO_UPPER]_WRITE)
            .action(CrudAction.PATCH, [SEU_CASO_DE_USO]Permissions.[SEU_CASO_DE_USO_UPPER]_WRITE)
            .action(CrudAction.DELETE, [SEU_CASO_DE_USO]Permissions.[SEU_CASO_DE_USO_UPPER]_WRITE)
            .build();

    public [SEU_CASO_DE_USO]Service([SEU_CASO_DE_USO]RepositoryPort repository,
                                AuditableInitializer auditableInitializer,
                                FieldMergeService fieldMergeService,
                                PermissionGuard permissionGuard) {
        super(repository, auditableInitializer, fieldMergeService, permissionGuard);
    }

    public [SEU_CASO_DE_USO]Service([SEU_CASO_DE_USO]RepositoryPort repository) {
        super(repository);
    }

    @Override
    protected ActionPermissionPolicy permissionPolicy() {
        return POLICY;
    }

    @Override
    protected void before(com.core.security.ActionKey action, Object... args) {
        if (action == CrudAction.CREATE && args != null && args.length > 0 && args[0] instanceof [SEU_CASO_DE_USO] domain) {
            prepare[SEU_CASO_DE_USO]ToCreate(domain);
        }
    }

    private static void prepare[SEU_CASO_DE_USO]ToCreate([SEU_CASO_DE_USO] domain) {
        if (domain.getActive() == null) domain.setActive(Boolean.TRUE);
    }
}
```

> Até aqui, **nenhuma** dependência de Spring/JPA/Web. É puro domínio.
```

> Até aqui, **nenhuma** dependência de Spring/JPA/Web. É puro domínio.

---

## 3) Infra/Adapters (JPA + Web + Config)

Esta seção detalha a implementação dos adapters e da infraestrutura, que dependem de frameworks como Spring e JPA.

**Pacote raiz da infra:** `com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].adapter` (no módulo `[SEU_MODULO_BASE]-infra`).

### 3.1. Entity JPA

Crie a classe de entidade JPA `adapter/persistence/entity/[SEU_CASO_DE_USO]Entity.java`. Substitua `[SEU_CASO_DE_USO]` pelo nome do seu caso de uso.

```java
package com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].adapter.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "[NOME_TABELA]",
        uniqueConstraints = @UniqueConstraint(name = "uk_[NOME_TABELA]_code", columnNames = "code"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class [SEU_CASO_DE_USO]Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String code; // Exemplo de campo
    @Column(nullable = false, length = 100)
    private String name; // Exemplo de campo
    @Column(length = 255)
    private String description; // Exemplo de campo
    @Column(nullable = false)
    private Boolean active; // Exemplo de campo

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
```

### 3.2. Repository Spring Data JPA

Crie a interface de repositório Spring Data JPA `adapter/persistence/repository/[SEU_CASO_DE_USO]JpaRepository.java`.

```java
package com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].adapter.persistence.repository;

import com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].adapter.persistence.entity.[SEU_CASO_DE_USO]Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface [SEU_CASO_DE_USO]JpaRepository extends JpaRepository<[SEU_CASO_DE_USO]Entity, Long> {
    // Exemplo de métodos de busca específicos
    boolean existsByCode(String code);
    Optional<[SEU_CASO_DE_USO]Entity> findByCode(String code);
}
```

### 3.3. Mapper (MapStruct) Entity ↔ Domain

Crie a interface do mapper MapStruct `adapter/persistence/mapper/[SEU_CASO_DE_USO]EntityMapper.java` para converter entre a entidade JPA e o domínio.

```java
package com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].adapter.persistence.mapper;

import com.core.infrastructure.mapper.GenericEntityMapper;
import com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].adapter.persistence.entity.[SEU_CASO_DE_USO]Entity;
import com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].domain.[SEU_CASO_DE_USO];
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface [SEU_CASO_DE_USO]EntityMapper
        extends GenericEntityMapper<[SEU_CASO_DE_USO], [SEU_CASO_DE_USO]Entity> {
}
```

### 3.4. Adapter do Port de Repositório

Crie a classe `adapter/persistence/[SEU_CASO_DE_USO]RepositoryAdapter.java` que implementa o port de repositório definido no domínio.

```java
package com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].adapter.persistence;

import com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].adapter.persistence.mapper.[SEU_CASO_DE_USO]EntityMapper;
import com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].adapter.persistence.repository.[SEU_CASO_DE_USO]JpaRepository;
import com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].domain.[SEU_CASO_DE_USO];
import com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].port.[SEU_CASO_DE_USO]RepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class [SEU_CASO_DE_USO]RepositoryAdapter implements [SEU_CASO_DE_USO]RepositoryPort {

    private final [SEU_CASO_DE_USO]JpaRepository repo;
    private final [SEU_CASO_DE_USO]EntityMapper mapper;

    @Override public [SEU_CASO_DE_USO] save([SEU_CASO_DE_USO] domain) {
        var saved = repo.save(mapper.toEntity(domain));
        return mapper.toDomain(saved);
    }

    @Override public Optional<[SEU_CASO_DE_USO]> findById(Long id) {
        return repo.findById(id).map(mapper::toDomain);
    }

    @Override public List<[SEU_CASO_DE_USO]> findAll() {
        return repo.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override public void deleteById(Long id) {
        repo.deleteById(id);
    }

    @Override public boolean existsByCode(String code) { return repo.existsByCode(code); }
    @Override public Optional<[SEU_CASO_DE_USO]> findByCode(String code) { return repo.findByCode(code).map(mapper::toDomain); }
}
```

### 3.5. DTOs Web (Request e Response)

Crie as classes DTO para requisição e resposta web: `adapter/web/dto/[SEU_CASO_DE_USO]RequestDTO.java` e `adapter/web/dto/[SEU_CASO_DE_USO]ResponseDTO.java`.

`adapter/web/dto/[SEU_CASO_DE_USO]RequestDTO.java`

```java
package com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].adapter.web.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class [SEU_CASO_DE_USO]RequestDTO {
    public String code; // Exemplo de campo
    public String name; // Exemplo de campo
    public String description; // Exemplo de campo
    public Boolean active; // Exemplo de campo
}
```

`adapter/web/dto/[SEU_CASO_DE_USO]ResponseDTO.java`

```java
package com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].adapter.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class [SEU_CASO_DE_USO]ResponseDTO {
    public Long id;
    public String code; // Exemplo de campo
    public String name; // Exemplo de campo
    public String description; // Exemplo de campo
    public Boolean active; // Exemplo de campo
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
}
```

### 3.6. Mapper Web (MapStruct) Domain ↔ DTO

Crie a interface do mapper MapStruct `adapter/web/mapper/[SEU_CASO_DE_USO]DtoMapper.java` para converter entre o domínio e os DTOs web.

```java
package com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].adapter.web.mapper;

import com.core.infrastructure.mapper.GenericDtoMapper;
import com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].adapter.web.dto.[SEU_CASO_DE_USO]RequestDTO;
import com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].adapter.web.dto.[SEU_CASO_DE_USO]ResponseDTO;
import com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].domain.[SEU_CASO_DE_USO];
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface [SEU_CASO_DE_USO]DtoMapper extends GenericDtoMapper<[SEU_CASO_DE_USO], [SEU_CASO_DE_USO]RequestDTO, [SEU_CASO_DE_USO]ResponseDTO> {
    @Override
    [SEU_CASO_DE_USO] toDomain([SEU_CASO_DE_USO]RequestDTO dto);

    @Override
    [SEU_CASO_DE_USO]ResponseDTO toResponse([SEU_CASO_DE_USO] domain);
}
```

### 3.7. Controller REST (usando o controller genérico)

Crie a classe do controller REST `adapter/web/[SEU_CASO_DE_USO]Controller.java`.

```java
package com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].adapter.web;

import com.core.infrastructure.web.GenericDtoRestController;
import com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].adapter.web.dto.[SEU_CASO_DE_USO]RequestDTO;
import com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].adapter.web.dto.[SEU_CASO_DE_USO]ResponseDTO;
import com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].adapter.web.mapper.[SEU_CASO_DE_USO]DtoMapper;
import com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].domain.[SEU_CASO_DE_USO];
import com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].usecase.[SEU_CASO_DE_USO]Service;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/[SEU_MODULO_BASE_MINUSCULO]/[SEU_CASO_DE_USO_PLURAL_MINUSCULO]")
@Tag(name="[SEU_CASO_DE_USO_PLURAL]", description="CRUD de [SEU_CASO_DE_USO_PLURAL_MINUSCULO]")
public class [SEU_CASO_DE_USO]Controller extends GenericDtoRestController<[SEU_CASO_DE_USO], Long, [SEU_CASO_DE_USO]RequestDTO, [SEU_CASO_DE_USO]ResponseDTO> {
    public [SEU_CASO_DE_USO]Controller([SEU_CASO_DE_USO]Service service, [SEU_CASO_DE_USO]DtoMapper mapper) { super(service, mapper); }
}
```

### 3.8. Configuração dos Beans do Use Case

Crie a classe de configuração `config/[SEU_CASO_DE_USO]Config.java` para registrar os beans do seu use case.

```java
package com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].config;

import com.core.usecase.AuditableInitializer;
import com.core.usecase.DefaultAuditableInitializer;
import com.core.usecase.FieldMergeService;
import com.core.usecase.PermissionGuard;
import com.core.usecase.ReflectiveFieldMergeService;
import com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].port.[SEU_CASO_DE_USO]RepositoryPort;
import com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].usecase.[SEU_CASO_DE_USO]Service;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class [SEU_CASO_DE_USO]Config {
    @Bean
    public [SEU_CASO_DE_USO]Service [SEU_CASO_DE_USO_MINUSCULO]Service([SEU_CASO_DE_USO]RepositoryPort repo, PermissionGuard guard) {
        AuditableInitializer aud = new DefaultAuditableInitializer();
        FieldMergeService merge = new ReflectiveFieldMergeService();
        return new [SEU_CASO_DE_USO]Service(repo, aud, merge, guard);
    }
}
```

### 3.9. Script DDL (Flyway)

Crie o script de migração de banco de dados `src/main/resources/db/migration/Vxxx__create_[NOME_TABELA].sql`. Substitua `Vxxx` por um número sequencial e `[NOME_TABELA]` pelo nome da tabela.

```plaintext
CREATE TABLE [NOME_TABELA] (
  id BIGSERIAL PRIMARY KEY,
  code VARCHAR(30) NOT NULL UNIQUE,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(255),
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX uk_[NOME_TABELA]_code ON [NOME_TABELA](code);
```

### 3.10. Configuração de Segurança Web (Autorização de Rota - Opcional)

No seu `SecurityFilterChain`, adicione a autorização para as rotas do seu novo módulo. Isso é opcional se a política padrão já for autenticar todas as rotas.

```java
.authorizeHttpRequests(auth -> auth
  .requestMatchers("/api/[SEU_MODULO_BASE_MINUSCULO]/[SEU_CASO_DE_USO_PLURAL_MINUSCULO]/**").authenticated()
  // ... resto das configurações
)
```

> As permissões **[SEU_CASO_DE_USO_UPPER]\_READ/[SEU_CASO_DE_USO_UPPER]\_WRITE** serão checadas no **domínio** pelo `PermissionGuard`.

### 3.11. Configuração do Permission Scanner

Inclua o pacote do use case do seu novo módulo nas propriedades de scan de permissões no arquivo `application.yml` (ou equivalente).

```plaintext
app:
  permissions:
    scan:
      enabled: true
      base-packages:
        - com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].usecase
        # ... outros pacotes
```

Rodando o projeto, o scanner deve encontrar a `POLICY` e registrar as permissões. Garanta que o **grupo ADMIN** recebe as permissões **[SEU_CASO_DE_USO_UPPER]\_READ/[SEU_CASO_DE_USO_UPPER]\_WRITE** (pelo scanner ou seed).

### 3.12. Seeders (Geração de Dados de Exemplo - OPCIONAL)

Se a propriedade `app.seed.enabled=true` estiver configurada, você pode criar uma classe seeder para popular dados de exemplo para o seu novo módulo.

`bootstrap/[SEU_CASO_DE_USO]SeedRunner.java`

```java
package com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].bootstrap;

import com.core.security.CredentialContextPort;
import com.core.security.CredentialPrincipal;
import com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].domain.[SEU_CASO_DE_USO];
import com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].usecase.[SEU_CASO_DE_USO]Service;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Executa o seed inicial de [SEU_CASO_DE_USO_PLURAL_MINUSCULO]
 * Deve rodar após o SecuritySeedRunner.
 */
@Component
@DependsOn("securitySeedRunner")
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
@RequiredArgsConstructor
public class [SEU_CASO_DE_USO]SeedRunner implements ApplicationRunner {

    private final [SEU_CASO_DE_USO]Service [SEU_CASO_DE_USO_MINUSCULO]Service;
    private final CredentialContextPort credentialContext;

    @Override
    public void run(ApplicationArguments args) {

        // Usa o principal SEED com permissões temporárias
        var seedPrincipal = new CredentialPrincipal() {
            @Override
            public Long getUserId() {
                return 0L;
            }

            @Override
            public String getUsername() {
                return "SEED";
            }

            @Override
            public List<String> getPermissions() {
                return java.util.List.of("[SEU_CASO_DE_USO_UPPER]_WRITE", "[SEU_CASO_DE_USO_UPPER]_READ");
            }
        };

        credentialContext.setAuthenticatedPrincipal(seedPrincipal);

        try {
            // Exemplo de dados para seed
            upsert[SEU_CASO_DE_USO]("CODE1", "Nome 1", "Descrição 1", true);
            upsert[SEU_CASO_DE_USO]("CODE2", "Nome 2", "Descrição 2", true);
        } finally {
            credentialContext.clear();
        }
    }

    /* ========================= UPSERT ========================= */

    private [SEU_CASO_DE_USO] upsert[SEU_CASO_DE_USO](String code, String name, String description, boolean active) {
        Optional<[SEU_CASO_DE_USO]> found = [SEU_CASO_DE_USO_MINUSCULO]Service.findAll().stream()
                .filter(c -> c.getCode().equalsIgnoreCase(code))
                .findFirst();

        if (found.isPresent()) {
            [SEU_CASO_DE_USO] existing = found.get();
            boolean changed = false;

            if (!equalsStr(existing.getName(), name)) { existing.setName(name); changed = true; }
            if (!equalsStr(existing.getDescription(), description)) { existing.setDescription(description); changed = true; }
            if (existing.getActive() != active) { existing.setActive(active); changed = true; }

            return changed ? [SEU_CASO_DE_USO_MINUSCULO]Service.patch(existing.getId(), existing) : existing;
        }

        [SEU_CASO_DE_USO] input = [SEU_CASO_DE_USO].builder()
                .code(code)
                .name(name)
                .description(description)
                .active(active)
                .build();

        return [SEU_CASO_DE_USO_MINUSCULO]Service.create(input);
    }

    private boolean equalsStr(String a, String b) {
        return (a == null && b == null) || (a != null && a.equals(b));
    }
}
```

### 3.13. Adição da Dependência do Módulo Infra no `pom.xml` do `app-api` (Módulo Raiz)

No `pom.xml` do módulo `app-api`, adicione a dependência do seu novo módulo de infraestrutura:

```xml
    <dependency>
      <groupId>br.ueg.appgenesis</groupId>
      <artifactId>appgenesis-security</artifactId>
      <version>1.0.0</version>
    </dependency>

    <dependency>
      <groupId>br.ueg.appgenesis</groupId>
      <artifactId>[SEU_MODULO_BASE]-infra</artifactId>
      <version>1.0.0</version>
    </dependency>
```

> Garante que os componentes da infraestrutura do novo módulo serão reconhecidos pelo Spring Boot.

### 3.14. Configuração `scanBasePackages` no `TripRequestApplication` (Módulo Raiz)

No seu `TripRequestApplication` (classe principal do Spring Boot), garanta que o pacote base do seu novo módulo seja incluído no `scanBasePackages`.

```java
@SpringBootApplication(scanBasePackages = {
        "com.core.*",
        "com.triprequest.*",
        "com.triprequest.[SEU_MODULO_BASE].*"
})
```

> Garante que os componentes da infraestrutura do novo módulo serão reconhecidos pelo Spring Boot.

---

## 4) Teste Rápido (Manual)

Esta seção fornece exemplos de comandos `curl` para testar manualmente os endpoints do seu novo módulo. Lembre-se de substituir os placeholders `[SEU_MODULO_BASE_MINUSCULO]`, `[SEU_CASO_DE_USO_PLURAL_MINUSCULO]`, `[ID_DO_REGISTRO]` e `<TOKEN>` pelos valores apropriados.

1.  **Login** e obtenha o token de autenticação.
2.  **Criar [SEU_CASO_DE_USO_MINUSCULO]**:

    ```plaintext
    curl -X POST http://localhost:8080/api/[SEU_MODULO_BASE_MINUSCULO]/[SEU_CASO_DE_USO_PLURAL_MINUSCULO] \
     -H "Authorization: Bearer <TOKEN>" -H "Content-Type: application/json" \
     -d '{"code":"[CODIGO]","name":"[NOME]","description":"[DESCRICAO]", "active":true}'
    ```

3.  **Listar [SEU_CASO_DE_USO_PLURAL_MINUSCULO]**:

    ```plaintext
    curl -H "Authorization: Bearer <TOKEN>" http://localhost:8080/api/[SEU_MODULO_BASE_MINUSCULO]/[SEU_CASO_DE_USO_PLURAL_MINUSCULO]
    ```

4.  **Detalhar [SEU_CASO_DE_USO_MINUSCULO]**:

    ```plaintext
    curl -H "Authorization: Bearer <TOKEN>" http://localhost:8080/api/[SEU_MODULO_BASE_MINUSCULO]/[SEU_CASO_DE_USO_PLURAL_MINUSCULO]/[ID_DO_REGISTRO]
    ```

5.  **Atualizar (PUT) [SEU_CASO_DE_USO_MINUSCULO]**:

    ```plaintext
    curl -X PUT http://localhost:8080/api/[SEU_MODULO_BASE_MINUSCULO]/[SEU_CASO_DE_USO_PLURAL_MINUSCULO]/[ID_DO_REGISTRO] \
     -H "Authorization: Bearer <TOKEN>" -H "Content-Type: application/json" \
     -d '{"code":"[CODIGO_ATUALIZADO]","name":"[NOME_ATUALIZADO]","description":"[DESCRICAO_ATUALIZADA]", "active":true}'
    ```

6.  **Patch (PATCH) [SEU_CASO_DE_USO_MINUSCULO]**:

    ```plaintext
    curl -X PATCH http://localhost:8080/api/[SEU_MODULO_BASE_MINUSCULO]/[SEU_CASO_DE_USO_PLURAL_MINUSCULO]/[ID_DO_REGISTRO] \
     -H "Authorization: Bearer <TOKEN>" -H "Content-Type: application/json" \
     -d '{"description":"[NOVA_DESCRICAO]"}'
    ```

7.  **Deletar [SEU_CASO_DE_USO_MINUSCULO]**:

    ```plaintext
    curl -X DELETE -H "Authorization: Bearer <TOKEN>" http://localhost:8080/api/[SEU_MODULO_BASE_MINUSCULO]/[SEU_CASO_DE_USO_PLURAL_MINUSCULO]/[ID_DO_REGISTRO]
    ```

Se faltar permissão, a resposta esperada deve ser `403` com `ProblemDetail`.

---

## 5) Checklist para Desenvolvedor Júnior

Este checklist serve para você verificar se todos os passos foram seguidos corretamente na criação do seu novo módulo.

*   Criei a pasta `[SEU_MODULO_BASE]` com os **dois submódulos** (`[SEU_MODULO_BASE]-domain` e `[SEU_MODULO_BASE]-infra`) e os adicionei ao `pom.xml` raiz.
*   O módulo **`[SEU_MODULO_BASE]-domain`** contém:
    *   `[SEU_CASO_DE_USO]` (modelo de domínio).
    *   `[SEU_CASO_DE_USO]RepositoryPort` (interface do port de repositório).
    *   `[SEU_CASO_DE_USO]Permissions` (constantes de permissão).
    *   `[SEU_CASO_DE_USO]Service` (use case com `ActionPermissionPolicy`).
*   O módulo **`[SEU_MODULO_BASE]-infra`** contém:
    *   `[SEU_CASO_DE_USO]Entity` (entidade JPA).
    *   `[SEU_CASO_DE_USO]JpaRepository` (repositório Spring Data JPA).
    *   `[SEU_CASO_DE_USO]EntityMapper` (mapper MapStruct para Entity ↔ Domain).
    *   `[SEU_CASO_DE_USO]RepositoryAdapter` (implementação do port de repositório).
    *   `[SEU_CASO_DE_USO]RequestDTO` e `[SEU_CASO_DE_USO]ResponseDTO` (DTOs web).
    *   `[SEU_CASO_DE_USO]DtoMapper` (mapper MapStruct para Domain ↔ DTO).
    *   `[SEU_CASO_DE_USO]Controller` (controller REST).
    *   `[SEU_CASO_DE_USO]Config` (configuração dos beans do use case).
    *   Script de migração Flyway (`Vxxx__create_[NOME_TABELA].sql`).
*   O **Permission Scanner** inclui o pacote `com.triprequest.[SEU_MODULO_BASE].[SUFIXO_PACOTE].usecase` nas propriedades `app.permissions.scan.base-packages`.
*   As **Permissões** `[SEU_CASO_DE_USO_UPPER]_READ` e `[SEU_CASO_DE_USO_UPPER]_WRITE` aparecem no banco de dados (via scanner/seed) e o grupo ADMIN as possui.
*   Os testes manuais via `curl` passaram com sucesso.
*   O Swagger mostra os endpoints do seu novo módulo em "[SEU_CASO_DE_USO_PLURAL]".

---

## Diagrama de Arquitetura de Módulos

O diagrama abaixo ilustra a estrutura de módulos do projeto, incluindo os módulos base (`app-api`, `appgenesis-architecture`, `appgenesis-security`) e como o seu **novo módulo** (`[SEU_MODULO_BASE]`) se integra, com seus submódulos de domínio e infraestrutura.

```mermaid
graph TD
    subgraph Módulos Base
        A[app-api] --> B(appgenesis-security)
        A --> C(appgenesis-architecture)
        B --> C
    end

    subgraph Seu Novo Módulo: [SEU_MODULO_BASE]
        D[[[SEU_MODULO_BASE]-infra]] --> E[[[SEU_MODULO_BASE]-domain]]
        D --> B
        D --> C
    end

    A --> D

    style A fill:#f9f,stroke:#333,stroke-width:2px
    style B fill:#bbf,stroke:#333,stroke-width:2px
    style C fill:#bfb,stroke:#333,stroke-width:2px
    style D fill:#ffb,stroke:#333,stroke-width:2px
    style E fill:#fbb,stroke:#333,stroke-width:2px
```

---

## 6) Dicas e Erros Comuns

* **MapStruct não gerando mappers** → faltou habilitar annotation processing no `maven-compiler-plugin` e/ou no IDE.
* **401/403 sem corpo** → confirme EntryPoint/AccessDenied custom com `ProblemDetail`.
* **Permissões não surgem** → verifique `app.permissions.scan.base-packages`.
* **Validação de domínio**: use as **anotações do domínio** no model, e `DomainValidator` já é chamado no `GenericCrudService`.
* **Separação**: nada de `@Entity`, `@Component`, `@RestController` no **domínio**.
* **app-api**: alterações nesse projeto estão demandado excutar clean install no pom.xml geral

Pronto. Com esse roteiro, um dev júnior consegue sair do zero e entregar o módulo `triprequest-base` com o caso de uso **Trip Category** completo, mantendo o **domínio agnóstico** e os **adapters** bem isolados.