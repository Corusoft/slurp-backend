package dev.corusoft.slurp.users.infrastructure.controllers;

import dev.corusoft.slurp.common.api.ApiResponse;
import dev.corusoft.slurp.common.exception.PermissionException;
import dev.corusoft.slurp.users.application.UserService;
import dev.corusoft.slurp.users.application.utils.AuthUtils;
import dev.corusoft.slurp.users.domain.User;
import dev.corusoft.slurp.users.domain.exceptions.UserIsDeactivatedException;
import dev.corusoft.slurp.users.domain.exceptions.UserNotFoundException;
import dev.corusoft.slurp.users.infrastructure.dto.input.UpdateContactInfoParamsDTO;
import dev.corusoft.slurp.users.infrastructure.dto.output.UserDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static dev.corusoft.slurp.common.api.ApiResponseHelper.buildEmptySuccessApiResponse;
import static dev.corusoft.slurp.common.api.ApiResponseHelper.buildSuccessApiResponse;
import static dev.corusoft.slurp.common.security.SecurityConstants.USER_ID_ATTRIBUTE_NAME;
import static dev.corusoft.slurp.users.infrastructure.dto.conversors.UserConversor.toUserDTO;

@RestController
@RequestMapping("/v1/users")
public class UserController {
    /* ******************** DEPENDENCIAS ******************** */
    private final UserService userService;
    private final AuthUtils authUtils;


    public UserController(UserService userService, AuthUtils authUtils) {
        this.userService = userService;
        this.authUtils = authUtils;
    }

    /* ******************** ENDPOINTS ******************** */
    @GetMapping(path = "/{userID}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<UserDTO> findUserById(
            @RequestAttribute(USER_ID_ATTRIBUTE_NAME) UUID userID,
            @PathVariable("userID") UUID pathUserID
    ) throws UserNotFoundException, UserIsDeactivatedException {
        // Actualizar información de contacto
        User user = userService.findUserByID(pathUserID);

        UserDTO userDTO = toUserDTO(user);
        return buildSuccessApiResponse(userDTO);
    }

    @PatchMapping(path = "/{userID}/contact",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<UserDTO> updateContactInfo(
            @RequestAttribute(USER_ID_ATTRIBUTE_NAME) UUID userID,
            @PathVariable("userID") UUID pathUserID,
            @Valid @RequestBody UpdateContactInfoParamsDTO params
    ) throws PermissionException, UserNotFoundException, UserIsDeactivatedException {
        // Comprobar que usuario actual y usuario objetivo son el mismo
        if (!authUtils.doUsersMatch(userID, pathUserID)) {
            throw new PermissionException();
        }

        // Actualizar información de contacto
        User user = userService.updateContactInfo(userID, params);

        UserDTO userDTO = toUserDTO(user);
        return buildSuccessApiResponse(userDTO);
    }

    @DeleteMapping(path = "/{userID}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deactivateUser(
            @RequestAttribute(USER_ID_ATTRIBUTE_NAME) UUID userID,
            @PathVariable("userID") UUID pathUserID
    ) throws PermissionException, UserNotFoundException {
        // Comprobar que usuario actual y usuario objetivo son el mismo
        if (!authUtils.doUsersMatch(userID, pathUserID)) {
            throw new PermissionException();
        }

        // Marcar usuario como inactivo
        userService.deactivateUser(userID);

        return buildEmptySuccessApiResponse();
    }

    @PostMapping(path = "/{userID}/activate")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<UserDTO> reactivateUser(
            @RequestAttribute(USER_ID_ATTRIBUTE_NAME) UUID userID,
            @PathVariable("userID") UUID pathUserID
    ) throws PermissionException, UserNotFoundException {
        // Comprobar que usuario actual y usuario objetivo son el mismo
        if (!authUtils.doUsersMatch(userID, pathUserID)) {
            throw new PermissionException();
        }

        // Marcar usuario como activo
        User user = userService.reactivateUser(userID);

        UserDTO userDTO = toUserDTO(user);
        return buildSuccessApiResponse(userDTO);
    }

}
