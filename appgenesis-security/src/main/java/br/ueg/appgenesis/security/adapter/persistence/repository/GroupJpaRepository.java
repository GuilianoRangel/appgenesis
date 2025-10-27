package br.ueg.appgenesis.security.adapter.persistence.repository;

import br.ueg.appgenesis.security.adapter.persistence.entity.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupJpaRepository extends JpaRepository<GroupEntity, Long> {}
