package dev.corusoft.slurp.common.api.error;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class ApiErrorDetails extends Exception {

    private String reason;

    public ApiErrorDetails(String reason) {
        super(reason);
        this.reason = reason;
    }
}
