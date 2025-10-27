package br.ueg.appgenesis.security.port.link;

import java.util.List;

public interface UserGroupLinkPort {
    boolean add(Long userId, Long groupId);
    boolean remove(Long userId, Long groupId);
    boolean exists(Long userId, Long groupId);
    List<Long> findGroupsByUser(Long userId);
    List<Long> findUsersByGroup(Long groupId);
}
