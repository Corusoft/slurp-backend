package dev.corusoft.slurp.users.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "userTable", schema = "users")
public class User {
    /* Atributes */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", nullable = false)
    private UUID userID;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "surname", length = 50)
    private String surname;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name = "birthdate", nullable = false)
    private LocalDate birthDate;

    @Builder.Default
    @Column(name = "registeredat", nullable = false)
    private LocalDateTime registeredAt = LocalDateTime.now();


    /* Relationships */
    @ToString.Exclude
    @OneToOne(mappedBy = "user", orphanRemoval = true)
    private Credential credential;

    @OneToOne(mappedBy = "user", orphanRemoval = true)
    private ContactInfo contactInfo;

    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<UserRole> userRoles = new ArrayList<>();


    /* Domain-Model */
    public void attachCredential(Credential other) {
        this.credential = other;
        other.setUser(this);
    }

    public void attachContactInfo(ContactInfo other) {
        this.contactInfo = other;
        other.setUser(this);
    }

    public User attachUserRole(Role other) {
        UserRole userRole = new UserRole();
        userRole.setId(new UserRoleID(this.userID, other.getRoleID()));
        userRole.setAssignedAt(LocalDateTime.now());
        userRole.setUser(this);
        userRole.setRole(other);

        return this;
    }

}
