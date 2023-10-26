package dev.corusoft.slurp.common.exception;

import jakarta.validation.constraints.NotNull;

public class EntityAlreadyExistsException extends AbstractEntityException {
    public EntityAlreadyExistsException(@NotNull String entityName, @NotNull Object key) {
        super(entityName, key);
    }
}
