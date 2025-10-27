package br.ueg.appgenesis.security.usecase;

import br.ueg.appgenesis.core.domain.validation.DomainValidationException;
import br.ueg.appgenesis.core.domain.validation.DomainViolation;
import br.ueg.appgenesis.security.domain.User;
import br.ueg.appgenesis.security.domain.view.UserProfileView;
import br.ueg.appgenesis.security.port.GroupRepositoryPort;
import br.ueg.appgenesis.security.port.PermissionRepositoryPort;
import br.ueg.appgenesis.security.port.UserRepositoryPort;
import br.ueg.appgenesis.security.port.link.GroupPermissionLinkPort;
import br.ueg.appgenesis.security.port.link.UserGroupLinkPort;

import java.util.ArrayList;
import java.util.List;

public class UserProfileService {

    private final UserRepositoryPort userRepo;
    private final GroupRepositoryPort groupRepo;
    private final PermissionRepositoryPort permRepo;
    private final UserGroupLinkPort userGroupLink;
    private final GroupPermissionLinkPort groupPermLink;

    public UserProfileService(UserRepositoryPort userRepo,
                              GroupRepositoryPort groupRepo,
                              PermissionRepositoryPort permRepo,
                              UserGroupLinkPort userGroupLink,
                              GroupPermissionLinkPort groupPermLink) {
        this.userRepo = userRepo;
        this.groupRepo = groupRepo;
        this.permRepo = permRepo;
        this.userGroupLink = userGroupLink;
        this.groupPermLink = groupPermLink;
    }

    public UserProfileView getProfile(Long userId) {
        var errors = new ArrayList<DomainViolation>();
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) {
            errors.add(new DomainViolation("userId", "usuário inexistente", "Exists"));
            throw new DomainValidationException(errors);
        }

        List<Long> groupIds = userGroupLink.findGroupsByUser(userId);
        var groups = groupIds.stream()
                .map(groupRepo::findById)
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .map(g -> UserProfileView.GroupSummary.builder()
                        .id(g.getId()).name(g.getName()).status(g.getStatus()).build())
                .toList();

        List<Long> permIds = groupIds.stream()
                .flatMap(gid -> groupPermLink.findPermissionsByGroup(gid).stream())
                .distinct()
                .toList();

        var permissions = permIds.stream()
                .map(permRepo::findById)
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .map(p -> UserProfileView.PermissionSummary.builder()
                        .id(p.getId()).code(p.getCode()).description(p.getDescription()).build())
                .toList();

        return UserProfileView.builder()
                .user(user)
                .groups(groups)
                .permissions(permissions)
                .build();
    }
}
