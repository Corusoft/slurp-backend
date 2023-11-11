package dev.corusoft.slurp.users.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "userrole", schema = "users")
public class UserRole {
    /* Atributes */
    @EmbeddedId
    private UserRoleID id;


    /* Relationships */
    @ToString.Exclude
    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ToString.Exclude
    @MapsId("roleId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Builder.Default
    @Column(name = "assignedat", nullable = false)
    private LocalDateTime assignedAt = LocalDateTime.now();


    /* Domain-Model */

}
