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
@Table(name = "contactinfo", schema = "users")
public class ContactInfo {
    /* Atributes */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contactinfo_id", nullable = false)
    private Long contactInfoID;

    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @Builder.Default
    @Column(name = "isemailverified", nullable = false)
    private Boolean isEmailVerified = false;

    @Column(name = "phonenumber", nullable = false, length = 20)
    private String phoneNumber;

    @Builder.Default
    @Column(name = "isphonenumberverified", nullable = false)
    private Boolean isPhoneNumberVerified = false;



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
        ContactInfo that = (ContactInfo) other;
        return Objects.equals(getContactInfoID(), that.getContactInfoID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getContactInfoID());
    }
}
