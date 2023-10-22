package dev.corusoft.slurp.users.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "Credential", schema = "users")
public class Credential {
    /* Atributes */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "credential_id", nullable = false)
    private Long credentialID;

    @Column(name = "nickname", nullable = false, unique = true)
    private String nickname;

    @Column(name = "passwordEncrypted", nullable = false)
    private String passwordEncrypted;

    @Column(name = "passwordSalt", nullable = false)
    private String passwordSalt;


    /* Relationships */
    @ToString.Exclude
    @OneToOne(optional = false, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;


    /* Domain-Model */

}
