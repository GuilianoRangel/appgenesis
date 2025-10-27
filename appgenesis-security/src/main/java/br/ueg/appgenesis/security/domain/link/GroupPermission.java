package br.ueg.appgenesis.security.domain.link;

import lombok.Value;

@Value
public class GroupPermission {
    Long groupId;
    Long permissionId;
}
