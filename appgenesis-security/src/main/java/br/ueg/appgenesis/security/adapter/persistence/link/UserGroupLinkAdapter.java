package br.ueg.appgenesis.security.adapter.persistence.link;

import br.ueg.appgenesis.security.port.link.UserGroupLinkPort;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class UserGroupLinkAdapter implements UserGroupLinkPort {

    private final UserGroupJpaRepository repo;
    public UserGroupLinkAdapter(UserGroupJpaRepository repo) { this.repo = repo; }

    @Override public boolean add(Long userId, Long groupId) {
        if (repo.existsByUserIdAndGroupId(userId, groupId)) return false;
        repo.save(UserGroupEntity.builder().userId(userId).groupId(groupId).build());
        return true;
    }
    @Override public boolean remove(Long userId, Long groupId) {
        if (!repo.existsByUserIdAndGroupId(userId, groupId)) return false;
        repo.deleteByUserIdAndGroupId(userId, groupId);
        return true;
    }
    @Override public boolean exists(Long userId, Long groupId) {
        return repo.existsByUserIdAndGroupId(userId, groupId);
    }
    @Override public List<Long> findGroupsByUser(Long userId) {
        return repo.findByUserId(userId).stream().map(UserGroupEntity::getGroupId).toList();
    }
    @Override public List<Long> findUsersByGroup(Long groupId) {
        return repo.findByGroupId(groupId).stream().map(UserGroupEntity::getUserId).toList();
    }
}
