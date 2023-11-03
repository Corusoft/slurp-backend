package dev.corusoft.slurp.users.infrastructure.dto.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticatedUserDTO {
    @JsonProperty(value = "serviceToken")
    private String serviceToken;

    @JsonProperty(value = "user")
    private UserDTO userDTO;
}
