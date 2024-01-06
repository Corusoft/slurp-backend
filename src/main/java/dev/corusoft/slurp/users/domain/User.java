package dev.corusoft.slurp.users.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    @Builder.Default
    @Column(name = "isactive", nullable = false)
    private Boolean isActive = true;

    @Column(name = "unactivesince")
    private LocalDateTime unactiveSince;


    /* Relationships */
    @ToString.Exclude
    @OneToOne(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    private Credential credential;

    @OneToOne(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    private ContactInfo contactInfo;

    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "user", orphanRemoval = true, fetch = FetchType.EAGER)
    private List<UserRole> userRoles = new ArrayList<>();


    /* Domain-Model */
    @Transient
    public void attachCredential(Credential other) {
        other.setUser(this);
        this.credential = other;
    }

    @Transient
    public void attachContactInfo(ContactInfo other) {
        other.setUser(this);
        this.contactInfo = other;
    }


    @Transient
    public List<UserRoles> getAttachedRoles() {
        return getUserRoles()
                .stream()
                .map(userRole -> userRole.getRole().getName())
                .collect(Collectors.toList());
    }

    @Transient
    public void markAsUnactive() {
        this.isActive = false;
        this.unactiveSince = LocalDateTime.now();
    }

    @Transient
    public void markAsActive() {
        this.isActive = true;
        this.unactiveSince = null;
    }

}
