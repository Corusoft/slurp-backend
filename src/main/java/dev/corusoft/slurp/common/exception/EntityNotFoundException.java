package dev.corusoft.slurp.common.exception;

public class EntityNotFoundException extends AbstractEntityException {
    public EntityNotFoundException(String entityName, Object key) {
        super(entityName, key);
    }
}
