package br.ueg.appgenesis.security.adapter.persistence;

import br.ueg.appgenesis.core.infrastructure.persistence.GenericJpaAdapter;
import br.ueg.appgenesis.security.adapter.persistence.entity.DepartmentEntity;
import br.ueg.appgenesis.security.adapter.persistence.repository.DepartmentJpaRepository;
import br.ueg.appgenesis.security.domain.Department;
import br.ueg.appgenesis.security.port.DepartmentRepositoryPort;
import br.ueg.appgenesis.security.adapter.mapper.DepartmentEntityMapper;
import org.springframework.stereotype.Component;

@Component
public class DepartmentRepositoryAdapter
        extends GenericJpaAdapter<Department, DepartmentEntity, Long>
        implements DepartmentRepositoryPort {

    public DepartmentRepositoryAdapter(DepartmentJpaRepository repository, DepartmentEntityMapper mapper) {
        super(repository, mapper);
    }
}
