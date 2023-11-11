package dev.corusoft.slurp.users.domain.exceptions;

import dev.corusoft.slurp.common.exception.EntityAlreadyExistsException;
import dev.corusoft.slurp.users.domain.User;
import jakarta.validation.constraints.NotNull;

public class UserAlreadyExistsException extends EntityAlreadyExistsException {
    public static final String USER_CLASSNAME = User.class.getSimpleName();

    public UserAlreadyExistsException(@NotNull Object key) {
        super(USER_CLASSNAME, key);
    }
}
