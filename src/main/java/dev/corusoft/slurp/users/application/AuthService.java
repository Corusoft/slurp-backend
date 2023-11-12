package dev.corusoft.slurp.users.application;

import dev.corusoft.slurp.users.domain.User;
import dev.corusoft.slurp.users.domain.exceptions.IncorrectLoginException;
import dev.corusoft.slurp.users.domain.exceptions.PasswordsDoNotMatchException;
import dev.corusoft.slurp.users.domain.exceptions.UserAlreadyExistsException;
import dev.corusoft.slurp.users.domain.exceptions.UserNotFoundException;
import dev.corusoft.slurp.users.infrastructure.dto.input.RegisterUserParamsDTO;

import java.util.UUID;

public interface AuthService {
    /**
     * Registra un nuevo usuario.
     *
     * @param paramsDTO Datos del usuario a registrar
     * @return Usuario registrado
     * @throws UserAlreadyExistsException Usuario ya existe
     */
    User register(RegisterUserParamsDTO paramsDTO) throws UserAlreadyExistsException;

    /**
     * Iniciar sesión en el sistema.
     * @param nickname Nombre de usuario
     * @param rawPassword Contraseña escrita por el usuario
     * @return Datos del usuario autenticado
     * @throws IncorrectLoginException Nickname o contraseña incorrectos
     */
    User login(String nickname, String rawPassword) throws IncorrectLoginException;

    /**
     * Cambiar contraseña del usuario.
     * @param userID ID del usuario
     * @param oldPassword Antigua contraseña
     * @param newPassword Nueva contraseña
     * @throws UserNotFoundException No se encuentra al usuario
     * @throws PasswordsDoNotMatchException Contraseñas no coinciden
     */
    void changePassword(UUID userID, String oldPassword, String newPassword)
            throws UserNotFoundException, PasswordsDoNotMatchException;

}
