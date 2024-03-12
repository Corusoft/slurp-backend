package dev.corusoft.slurp.common.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InvalidArgumentException extends Exception {
    private String objectName;

    private String field;

    private Object value;

    private String reason;

    public InvalidArgumentException(String objectName, String field, Object value, String reason) {
        super(reason);
        this.objectName = objectName;
        this.field = field;
        this.value = value;
        this.reason = reason;
    }

}
