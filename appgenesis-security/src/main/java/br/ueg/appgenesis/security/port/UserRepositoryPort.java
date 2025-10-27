package br.ueg.appgenesis.security.port;
import br.ueg.appgenesis.core.port.GenericRepositoryPort;
import br.ueg.appgenesis.security.domain.User;
import java.util.Optional;
public interface UserRepositoryPort extends GenericRepositoryPort<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
}
