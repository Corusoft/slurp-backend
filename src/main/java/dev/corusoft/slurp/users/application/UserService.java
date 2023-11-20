package dev.corusoft.slurp.users.application;

import dev.corusoft.slurp.users.domain.User;
import dev.corusoft.slurp.users.domain.exceptions.UserIsDeactivatedException;
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
     * @throws UserIsDeactivatedException Usuario está desactivado
     */
    User findUserByID(UUID userID) throws UserNotFoundException, UserIsDeactivatedException;

    /**
     * Actualiza la información de contacto del usuario
     *
     * @param userID       ID del usuario
     * @param updateParams Datos de contacto a actualizar
     * @return Usuario con los datos actualizados
     * @throws UserNotFoundException No se encuentra al usuario
     * @throws UserIsDeactivatedException Usuario está desactivado
     */
    User updateContactInfo(UUID userID, UpdateContactInfoParamsDTO updateParams) throws UserNotFoundException, UserIsDeactivatedException;

    /**
     * Marca un usuario como desactivado.
     * Sus datos permanecen en el sistema un plazo de tiempo determinado,
     * pero ya no se considerará como un usuario activo.
     *
     * @param userID ID del usuario
     * @throws UserNotFoundException No se encuentra al usuario
     */
    void deactivateUser(UUID userID) throws UserNotFoundException;

    /**
     * Marca un usuario como activo.
     * Indica que un usuario ha reactivado su cuenta.
     *
     * @param userID ID del usuario
     * @return Usuario con los datos actualizados
     * @throws UserNotFoundException No se encuentra al usuario
     */
    User reactivateUser(UUID userID) throws UserNotFoundException;

}
