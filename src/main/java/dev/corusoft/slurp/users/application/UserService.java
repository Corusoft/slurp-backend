package dev.corusoft.slurp.users.application;

import dev.corusoft.slurp.common.domain.exceptions.EntityAlreadyExistsException;
import dev.corusoft.slurp.users.domain.User;
import dev.corusoft.slurp.users.infrastructure.dto.input.RegisterUserParamsDTO;

public interface UserService {
    /**
     * Registra un nuevo usuario.
     *
     * @param paramsDTO Datos del usuario a registrar
     * @return Usuario registrado
     * @throws EntityAlreadyExistsException Usuario ya existe
     */
    User signUp(RegisterUserParamsDTO paramsDTO) throws EntityAlreadyExistsException;
}
