package br.ueg.appgenesis.security.adapter.persistence;

import br.ueg.appgenesis.core.infrastructure.persistence.GenericJpaAdapter;
import br.ueg.appgenesis.security.adapter.persistence.entity.UserEntity;
import br.ueg.appgenesis.security.adapter.persistence.repository.UserJpaRepository;
import br.ueg.appgenesis.security.domain.User;
import br.ueg.appgenesis.security.port.UserRepositoryPort;
import br.ueg.appgenesis.security.adapter.mapper.UserEntityMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserRepositoryAdapter
        extends GenericJpaAdapter<User, UserEntity, Long>
        implements UserRepositoryPort {

    protected UserJpaRepository userJpaRepository;

    public UserRepositoryAdapter(UserJpaRepository repository, UserEntityMapper mapper) {
        super(repository, mapper);
        this.userJpaRepository = repository;
    }

    @Override public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username).map(mapper::toDomain);
    }
    @Override public boolean existsByEmail(String email) { return userJpaRepository.existsByEmail(email); }
}
