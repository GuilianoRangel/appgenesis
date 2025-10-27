package br.ueg.appgenesis.security.adapter.persistence.link;

import lombok.*;
import java.io.Serializable;

@Data @NoArgsConstructor @AllArgsConstructor
public class UserGroupId implements Serializable {
    private Long userId;
    private Long groupId;
}
