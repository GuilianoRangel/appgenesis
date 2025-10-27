package br.ueg.appgenesis.security.port.link;

import java.util.List;

public interface GroupPermissionLinkPort {
    boolean add(Long groupId, Long permissionId);
    boolean remove(Long groupId, Long permissionId);
    boolean exists(Long groupId, Long permissionId);
    List<Long> findPermissionsByGroup(Long groupId);
    List<Long> findGroupsByPermission(Long permissionId);
}
