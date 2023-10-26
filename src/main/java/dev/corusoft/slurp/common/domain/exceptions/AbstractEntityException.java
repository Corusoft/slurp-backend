package dev.corusoft.slurp.common.domain.exceptions;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AbstractEntityException extends Exception {
    @NotNull
    private String entityName;

    @NotNull
    private Object key;
}
