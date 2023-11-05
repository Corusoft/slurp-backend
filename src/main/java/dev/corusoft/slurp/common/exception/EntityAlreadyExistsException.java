package dev.corusoft.slurp.common.exception;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public class EntityAlreadyExistsException extends AbstractEntityException implements Serializable {
    public EntityAlreadyExistsException(@NotNull Object key) {
        super(null, key);
    }

    public EntityAlreadyExistsException(@NotNull String entityName, @NotNull Object key) {
        super(entityName, key);
    }
}
