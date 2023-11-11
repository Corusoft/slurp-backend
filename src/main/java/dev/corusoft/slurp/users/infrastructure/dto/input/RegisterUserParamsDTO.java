package dev.corusoft.slurp.users.infrastructure.dto.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.corusoft.slurp.users.domain.Gender;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterUserParamsDTO {
    // User
    @NotBlank
    @Size(min = 1, max = 50)
    private String name;

    @NotBlank
    @Size(min = 1, max = 50)
    private String surname;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Gender gender;

    @NotNull
    @PastOrPresent
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate birthDate;

    // Credentials
    @NotBlank
    @Size(min = 1, max = 50)
    private String nickname;

    @NotBlank
    @Size(min = 1)
    private String rawPassword;

    // ContactInfo
    @NotBlank
    @Email
    @Size(min = 1, max = 100)
    private String email;

    @NotBlank
    @Size(min = 1, max = 20)
    private String phoneNumber;
}
