package dev.corusoft.slurp.common.api.error;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ApiValidationErrorDetails extends ApiErrorDetails {

    private String objectName;

    private String fieldName;

    private Object rejectedValue;

    private String reason;
}
