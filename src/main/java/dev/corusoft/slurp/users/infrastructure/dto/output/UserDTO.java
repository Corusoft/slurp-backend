package dev.corusoft.slurp.users.infrastructure.dto.output;

import dev.corusoft.slurp.users.domain.Gender;
import dev.corusoft.slurp.users.domain.UserRoles;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class UserDTO {

    private UUID userID;

    @NotBlank()
    @Size(min = 1, max = 50)
    private String name;

    @NotNull
    @Size(min = 1, max = 50)
    private String surname;

    @NotNull
    private Gender gender;

    @NotBlank
    @Size(min = 1, max = 30)
    private String nickname;

    @PastOrPresent
    private LocalDate birthDate;

    @PastOrPresent
    private LocalDateTime registeredAt;

    private List<UserRoles> roles;

    private ContactInfoDTO contactInfo;

}

