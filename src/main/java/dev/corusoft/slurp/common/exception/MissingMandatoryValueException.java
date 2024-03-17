package dev.corusoft.slurp.common.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MissingMandatoryValueException extends Exception {
    private String fieldName;
}
