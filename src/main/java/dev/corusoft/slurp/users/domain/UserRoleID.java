package dev.corusoft.slurp.users.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
@Embeddable
public class UserRoleID implements Serializable {
    private static final long serialVersionUID = -8317904110740562813L;

    @Column(name = "user_id", nullable = false)
    private UUID userID;

    @Column(name = "role_id", nullable = false)
    private Long roleID;

}
