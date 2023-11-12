package dev.corusoft.slurp.users.infrastructure.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordParamsDTO {
    @NotBlank
    @Size(min = 1)
    private String oldPassword;

    @NotBlank
    @Size(min = 1)
    private String newPassword;
}
