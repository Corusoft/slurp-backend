package dev.corusoft.slurp.users.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "role", schema = "users")
public class Role {
    /* Atributes */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id", nullable = false)
    private Long roleID;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRoles name;


    /* Relationships */
    @ToString.Exclude
    @OneToMany(mappedBy = "role")
    private Set<UserRole> userRoles = new LinkedHashSet<>();


    /* Domain-Model */

}
