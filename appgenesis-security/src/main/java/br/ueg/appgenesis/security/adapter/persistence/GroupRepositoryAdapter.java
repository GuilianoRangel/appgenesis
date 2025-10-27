package br.ueg.appgenesis.security.adapter.persistence;

import br.ueg.appgenesis.core.infrastructure.persistence.GenericJpaAdapter;
import br.ueg.appgenesis.security.adapter.persistence.entity.GroupEntity;
import br.ueg.appgenesis.security.adapter.persistence.repository.GroupJpaRepository;
import br.ueg.appgenesis.security.domain.Group;
import br.ueg.appgenesis.security.port.GroupRepositoryPort;
import br.ueg.appgenesis.security.adapter.mapper.GroupEntityMapper;
import org.springframework.stereotype.Component;

@Component
public class GroupRepositoryAdapter
        extends GenericJpaAdapter<Group, GroupEntity, Long>
        implements GroupRepositoryPort {

    public GroupRepositoryAdapter(GroupJpaRepository repository, GroupEntityMapper mapper) {
        super(repository, mapper);
    }
}
