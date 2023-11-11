package dev.corusoft.slurp.common.exception;

import java.io.Serializable;

public class EntityNotFoundException extends AbstractEntityException implements Serializable {
    public EntityNotFoundException(String entityName, Object key) {
        super(entityName, key);
    }
}
