package dev.corusoft.slurp.common.api.error;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ApiValidationErrorDetails extends ApiErrorDetails {

    private String objectName;

    private String fieldName;

    private Object rejectedValue;

    public ApiValidationErrorDetails(String objectName, String fieldName, Object rejectedValue, String reason) {
        super(reason);
        this.objectName = objectName;
        this.fieldName = fieldName;
        this.rejectedValue = rejectedValue;
    }
}
