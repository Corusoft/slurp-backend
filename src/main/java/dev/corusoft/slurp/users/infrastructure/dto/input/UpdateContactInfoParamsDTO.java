package dev.corusoft.slurp.users.infrastructure.dto.input;

import jakarta.validation.constraints.Email;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateContactInfoParamsDTO {
    @Email
    private String newEmail;

    private String newMobilePhone;
}
