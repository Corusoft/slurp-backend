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
@Table(name = "systemsettings", schema = "users")
public class SystemSettiing {
    /* Atributes */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "setting_id", nullable = false)
    private Long settingID;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "value", nullable = false)
    private String value;


    /* Relationships */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;


    /* Domain-Model */

}
