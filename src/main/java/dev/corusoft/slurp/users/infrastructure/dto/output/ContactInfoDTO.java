package dev.corusoft.slurp.users.infrastructure.dto.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContactInfoDTO {
    private Long contactInfoID;

    @NotBlank(groups = {UserDTO.MandatoryField.class})
    @Email
    private String email;

    @NotNull
    @JsonProperty(value = "isEmailVerified")
    private Boolean isEmailVerified;

    private String phoneNumber;

    @NotNull
    @JsonProperty(value = "isPhoneNumberVerified")
    private Boolean isPhoneNumberVerified;
}
