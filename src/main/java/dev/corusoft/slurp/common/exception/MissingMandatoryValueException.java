package dev.corusoft.slurp.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MissingMandatoryValueException extends Exception {
    private final String field;
}
