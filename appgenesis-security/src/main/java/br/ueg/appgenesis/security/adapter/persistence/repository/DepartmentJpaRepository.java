package br.ueg.appgenesis.security.adapter.persistence.repository;

import br.ueg.appgenesis.security.adapter.persistence.entity.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentJpaRepository extends JpaRepository<DepartmentEntity, Long> {
}
