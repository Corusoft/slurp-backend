package dev.corusoft.slurp.common.error;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiValidationError implements ApiError {

    private String objectName;

    private String fieldName;

    private Object rejectedValue;

    private String reason;
}
