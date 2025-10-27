package br.ueg.appgenesis.security.adapter.persistence.link;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GroupPermissionJpaRepository extends JpaRepository<GroupPermissionEntity, GroupPermissionId> {
    boolean existsByGroupIdAndPermissionId(Long groupId, Long permissionId);
    void deleteByGroupIdAndPermissionId(Long groupId, Long permissionId);
    List<GroupPermissionEntity> findByGroupId(Long groupId);
    List<GroupPermissionEntity> findByPermissionId(Long permissionId);
}
