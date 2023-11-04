package dev.corusoft.slurp.users.infrastructure.dto.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.util.Objects;

@Data
@Builder
public class ContactInfoDTO {
    private Long contactInfoID;

    @NotBlank
    @Email
    private String email;

    @NotNull
    @JsonProperty(value = "isEmailVerified")
    private Boolean isEmailVerified;

    private String phoneNumber;

    @NotNull
    @JsonProperty(value = "isPhoneNumberVerified")
    private Boolean isPhoneNumberVerified;


    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        ContactInfoDTO that = (ContactInfoDTO) other;
        return Objects.equals(getContactInfoID(), that.getContactInfoID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getContactInfoID());
    }
}
