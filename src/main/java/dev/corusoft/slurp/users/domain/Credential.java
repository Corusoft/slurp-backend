package dev.corusoft.slurp.users.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

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

    @Column(name = "nickname", nullable = false, unique = true, length = 50)
    private String nickname;

    @Column(name = "passwordEncrypted", nullable = false)
    private String passwordEncrypted;


    /* Relationships */
    @ToString.Exclude
    @OneToOne(optional = false, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;


    /* Domain-Model */


    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Credential that = (Credential) other;
        return Objects.equals(getCredentialID(), that.getCredentialID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCredentialID());
    }
}
