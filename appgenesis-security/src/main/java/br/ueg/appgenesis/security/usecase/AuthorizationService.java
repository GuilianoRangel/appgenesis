package br.ueg.appgenesis.security.usecase;

import br.ueg.appgenesis.security.port.link.GroupPermissionLinkPort;
import br.ueg.appgenesis.security.port.link.UserGroupLinkPort;
import br.ueg.appgenesis.security.port.PermissionRepositoryPort;

import java.util.List;
import java.util.stream.Collectors;

public class AuthorizationService {

    private final UserGroupLinkPort userGroupLink;
    private final GroupPermissionLinkPort groupPermLink;
    private final PermissionRepositoryPort permRepo;

    public AuthorizationService(UserGroupLinkPort userGroupLink, GroupPermissionLinkPort groupPermLink, PermissionRepositoryPort permRepo) {
        this.userGroupLink = userGroupLink;
        this.groupPermLink = groupPermLink;
        this.permRepo = permRepo;
    }

    public List<String> permissionsOfUser(Long userId) {
        var groupIds = userGroupLink.findGroupsByUser(userId);
        var permIds = groupIds.stream()
                .flatMap(gid -> groupPermLink.findPermissionsByGroup(gid).stream())
                .distinct()
                .toList();
        return permIds.stream()
                .map(permRepo::findById)
                .filter(java.util.Optional::isPresent)
                .map(opt -> opt.get().getCode())
                .collect(Collectors.toList());
    }
}
