package dev.corusoft.slurp.common.error;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiValidationErrorDetails implements ApiErrorDetails {

    private String objectName;

    private String fieldName;

    private Object rejectedValue;

    private String reason;
}
