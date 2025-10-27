package br.ueg.appgenesis.security.adapter.persistence.link;

import br.ueg.appgenesis.security.port.link.GroupPermissionLinkPort;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class GroupPermissionLinkAdapter implements GroupPermissionLinkPort {

    private final GroupPermissionJpaRepository repo;
    public GroupPermissionLinkAdapter(GroupPermissionJpaRepository repo) { this.repo = repo; }

    @Override public boolean add(Long groupId, Long permissionId) {
        if (repo.existsByGroupIdAndPermissionId(groupId, permissionId)) return false;
        repo.save(GroupPermissionEntity.builder().groupId(groupId).permissionId(permissionId).build());
        return true;
    }
    @Override public boolean remove(Long groupId, Long permissionId) {
        if (!repo.existsByGroupIdAndPermissionId(groupId, permissionId)) return false;
        repo.deleteByGroupIdAndPermissionId(groupId, permissionId);
        return true;
    }
    @Override public boolean exists(Long groupId, Long permissionId) {
        return repo.existsByGroupIdAndPermissionId(groupId, permissionId);
    }
    @Override public List<Long> findPermissionsByGroup(Long groupId) {
        return repo.findByGroupId(groupId).stream().map(GroupPermissionEntity::getPermissionId).toList();
    }
    @Override public List<Long> findGroupsByPermission(Long permissionId) {
        return repo.findByPermissionId(permissionId).stream().map(GroupPermissionEntity::getGroupId).toList();
    }
}
