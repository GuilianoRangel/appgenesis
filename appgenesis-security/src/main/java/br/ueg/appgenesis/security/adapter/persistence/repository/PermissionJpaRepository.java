package br.ueg.appgenesis.security.adapter.persistence.repository;

import br.ueg.appgenesis.security.adapter.persistence.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PermissionJpaRepository extends JpaRepository<PermissionEntity, Long> {
    Optional<PermissionEntity> findByCode(String code);
}
