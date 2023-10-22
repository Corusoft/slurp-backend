package dev.corusoft.slurp.users.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Accessors(prefix = {"is", "has"})
@Entity
@Table(name = "contactinfo", schema = "users")
public class Contactinfo {
    /* Atributes */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contactinfo_id", nullable = false)
    private Long contactInfoID;

    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "isemailverified", nullable = false)
    private Boolean isEmailVerified = false;

    @Column(name = "phonenumber", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "isphonenumberverified", nullable = false)
    private Boolean isPhoneNumberVerified = false;


    /* Relationships */
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    /* Domain-Model */

}
