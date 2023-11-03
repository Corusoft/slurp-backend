package dev.corusoft.slurp.users.application;

import dev.corusoft.slurp.common.exception.EntityAlreadyExistsException;
import dev.corusoft.slurp.users.domain.User;
import dev.corusoft.slurp.users.infrastructure.dto.input.RegisterUserParamsDTO;

public interface AuthService {
    /**
     * Registra un nuevo usuario.
     *
     * @param paramsDTO Datos del usuario a registrar
     * @return Usuario registrado
     * @throws EntityAlreadyExistsException Usuario ya existe
     */
    User register(RegisterUserParamsDTO paramsDTO) throws EntityAlreadyExistsException;
}
