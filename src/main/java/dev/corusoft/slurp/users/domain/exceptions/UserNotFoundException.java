package dev.corusoft.slurp.users.domain.exceptions;

import dev.corusoft.slurp.common.exception.EntityNotFoundException;
import dev.corusoft.slurp.users.domain.User;

public class UserNotFoundException extends EntityNotFoundException {
    public static final String USER_CLASSNAME = User.class.getSimpleName();

    public UserNotFoundException(Object key) {
        super(USER_CLASSNAME, key);
    }
}
