package dev.corusoft.slurp.users.application;

import dev.corusoft.slurp.users.domain.User;
import dev.corusoft.slurp.users.domain.exceptions.UserNotFoundException;
import dev.corusoft.slurp.users.infrastructure.dto.input.UpdateContactInfoParamsDTO;

import java.util.UUID;

public interface UserService {
    /**
     * Buscar un usuario por su ID.
     *
     * @param userID ID del usuario
     * @return Datos del usuario
     * @throws UserNotFoundException No se encuentra al usuario
     */
    User findUserByID(UUID userID) throws UserNotFoundException;

    /**
     * Actualiza la información de contacto del usuario
     *
     * @param userID       ID del usuario
     * @param updateParams Datos de contacto a actualizar
     * @return Usuario con los datos actualizados
     * @throws UserNotFoundException No se encuentra al usuario
     */
    User updateContactInfo(UUID userID, UpdateContactInfoParamsDTO updateParams) throws UserNotFoundException;


}
