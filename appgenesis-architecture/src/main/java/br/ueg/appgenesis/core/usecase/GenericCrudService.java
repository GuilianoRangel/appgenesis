package br.ueg.appgenesis.core.usecase;

import br.ueg.appgenesis.core.domain.error.NotFoundException;
import br.ueg.appgenesis.core.domain.pagination.PaginatedResult;
import br.ueg.appgenesis.core.domain.pagination.PagingRequest;
import br.ueg.appgenesis.core.domain.validation.DomainValidator;
import br.ueg.appgenesis.core.domain.validation.support.DomainFieldValidator;
import br.ueg.appgenesis.core.port.GenericRepositoryPort;
import br.ueg.appgenesis.core.port.GenericServicePort;
import br.ueg.appgenesis.core.security.CrudAction;
import br.ueg.appgenesis.core.usecase.support.*;
import br.ueg.appgenesis.core.usecase.support.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import java.util.List;

public abstract class GenericCrudService<T, ID>
        extends ActionSecuredService
        implements GenericServicePort<T, ID> {

    protected final GenericRepositoryPort<T, ID> repository;
    protected final AuditableInitializer auditableInitializer;
    protected final FieldMergeService fieldMergeService;
    private final Class<T> domainClass;

    /** Construtor completo (guard opcional via null). */
    protected GenericCrudService(GenericRepositoryPort<T, ID> repository,
                                 AuditableInitializer auditableInitializer,
                                 FieldMergeService fieldMergeService,
                                 PermissionGuard permissionGuard) {
        super(permissionGuard);
        this.repository = repository;
        this.auditableInitializer = auditableInitializer;
        this.fieldMergeService = fieldMergeService;
        this.domainClass = inferDomainClass();
    }

    protected GenericCrudService(GenericRepositoryPort<T, ID> repository,
                                 AuditableInitializer auditableInitializer,
                                 FieldMergeService fieldMergeService) {
        this(repository, auditableInitializer, fieldMergeService, null);
    }

    protected GenericCrudService(GenericRepositoryPort<T, ID> repository) {
        this(repository, new DefaultAuditableInitializer(), new ReflectiveFieldMergeService(), null);
    }

    @SuppressWarnings("unchecked")
    private Class<T> inferDomainClass() {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof ParameterizedType) {
            return (Class<T>) ((ParameterizedType) superclass).getActualTypeArguments()[0];
        }
        throw new IllegalStateException("Não foi possível inferir a classe de domínio. " +
                                        "Certifique-se de que GenericCrudService é estendido com um tipo genérico concreto.");
    }

    /* ===================== Implementação de CRUD ===================== */

    private T doCreate(T entity) {
        DomainValidator.validateOrThrow(entity);
        auditableInitializer.onCreate(entity);
        return repository.save(entity);
    }

    /** PUT: substitui todo o estado público (exceto internos), permitindo null. */
    private T doUpdateReplace(ID id, T delta) {
        T current = repository.findById(id).orElseThrow(() -> new NotFoundException("Entidade não encontrada"));
        fieldMergeService.merge(delta, current, MergeMode.REPLACE_ALL);
        DomainValidator.validateOrThrow(current); // valida todas as regras do domínio
        auditableInitializer.onUpdate(current);
        return repository.save(current);
    }

    /** PATCH: aplica apenas valores presentes (não-nulos), preservando ausentes. */
    private T doUpdatePresentData(ID id, T delta) {
        T current = repository.findById(id).orElseThrow(() -> new NotFoundException("Entidade não encontrada"));
        fieldMergeService.merge(delta, current, MergeMode.PRESENT_ONLY);
        DomainValidator.validateOrThrow(current); // valida todas as regras do domínio
        auditableInitializer.onUpdate(current);
        return repository.save(current);
    }

    private T doFind(ID id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Entidade não encontrada"));
    }

    private List<T> doFindAll() {
        return repository.findAll();
    }

    private PaginatedResult<T> doFindAll(PagingRequest pagingRequest) {
        DomainFieldValidator.validateFieldExists(this.domainClass, pagingRequest.getSortBy());
        return repository.findAll(pagingRequest);
    }

    private void doDelete(ID id) {
        repository.deleteById(id);
    }

    /* ===================== API pública (usa execute/run + ações padrão) ===================== */

    @Override
    public T create(T entity) {
        return execute(CrudAction.CREATE, () -> doCreate(entity), entity);
    }

    @Override
    public T update(ID id, T delta) {
        return execute(CrudAction.UPDATE, () -> doUpdateReplace(id, delta),id, delta);
    }

    // Mantém para atualizar apenas conteudo enviado:
    @Override
    public T patch(ID id, T delta) {
        return execute(CrudAction.PATCH, () -> doUpdatePresentData(id, delta), id, delta);
    }

    @Override
    public T find(ID id) {
        return execute(CrudAction.READ, () -> doFind(id), id);
    }

    @Override
    public List<T> findAll() {
        return execute(CrudAction.LIST, this::doFindAll);
    }

    @Override
    public PaginatedResult<T> findAll(PagingRequest pagingRequest) {
        return execute(CrudAction.LIST, () -> doFindAll(pagingRequest), pagingRequest);
    }

    @Override
    public void delete(ID id) {
        run(CrudAction.DELETE, () -> doDelete(id), id);
    }
}
