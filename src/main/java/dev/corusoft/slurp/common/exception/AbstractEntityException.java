package dev.corusoft.slurp.common.exception;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class AbstractEntityException extends Exception implements Serializable {
    @NotNull
    private final String entityName;

    @NotNull
    private final Object key;
}
