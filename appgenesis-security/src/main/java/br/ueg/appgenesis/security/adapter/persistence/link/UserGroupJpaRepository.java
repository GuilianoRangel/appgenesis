package br.ueg.appgenesis.security.adapter.persistence.link;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserGroupJpaRepository extends JpaRepository<UserGroupEntity, UserGroupId> {
    boolean existsByUserIdAndGroupId(Long userId, Long groupId);
    void deleteByUserIdAndGroupId(Long userId, Long groupId);
    List<UserGroupEntity> findByUserId(Long userId);
    List<UserGroupEntity> findByGroupId(Long groupId);
}
