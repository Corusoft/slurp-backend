package dev.corusoft.slurp.users.infrastructure.controllers;

import dev.corusoft.slurp.common.api.ApiResponse;
import dev.corusoft.slurp.common.exception.PermissionException;
import dev.corusoft.slurp.common.security.jwt.application.JwtGenerator;
import dev.corusoft.slurp.common.security.jwt.domain.JwtData;
import dev.corusoft.slurp.users.application.AuthService;
import dev.corusoft.slurp.users.application.utils.AuthUtils;
import dev.corusoft.slurp.users.domain.User;
import dev.corusoft.slurp.users.domain.exceptions.*;
import dev.corusoft.slurp.users.infrastructure.dto.input.*;
import dev.corusoft.slurp.users.infrastructure.dto.output.AuthenticatedUserDTO;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

import static dev.corusoft.slurp.common.api.ApiResponseHelper.buildEmptySuccessApiResponse;
import static dev.corusoft.slurp.common.api.ApiResponseHelper.buildSuccessApiResponse;
import static dev.corusoft.slurp.common.security.SecurityConstants.TOKEN_ATTRIBUTE_NAME;
import static dev.corusoft.slurp.common.security.SecurityConstants.USER_ID_ATTRIBUTE_NAME;
import static dev.corusoft.slurp.users.infrastructure.dto.conversors.UserConversor.toAuthenticatedUserDTO;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {
    /* ******************** DEPENDENCIAS ******************** */
    private final AuthService authService;
    private final JwtGenerator jwtGenerator;
    private final AuthUtils authUtils;


    public AuthController(AuthService authService,
                          JwtGenerator jwtGenerator,
                          AuthUtils authUtils) {
        this.authService = authService;
        this.jwtGenerator = jwtGenerator;
        this.authUtils = authUtils;
    }


    /* ******************** ENDPOINTS ******************** */

    @PostMapping(path = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<AuthenticatedUserDTO>> register(
            @Validated @RequestBody RegisterUserParamsDTO params
    ) throws UserAlreadyExistsException {
        // Registrar usuario
        User registeredUser = authService.register(params);

        // Generar contenido de respuesta
        URI resourceLocation = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/v1/users/{userID}")
                .buildAndExpand(registeredUser.getUserID())
                .toUri();
        String serviceToken = authUtils.generateJWTFromUser(registeredUser);
        AuthenticatedUserDTO authenticatedUserDTO = toAuthenticatedUserDTO(serviceToken, registeredUser);

        // Crear respuesta y enviar
        ApiResponse<AuthenticatedUserDTO> body = buildSuccessApiResponse(authenticatedUserDTO);

        return ResponseEntity
                .created(resourceLocation)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @PostMapping(path = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ApiResponse<AuthenticatedUserDTO> login(
            @Validated @RequestBody LoginParamsDTO params
    ) throws IncorrectLoginException {
        // Iniciar sesión en servicio
        User user = authService.login(params.getNickname(), params.getPassword());

        // Generar token para usaurio
        String serviceToken = authUtils.generateJWTFromUser(user);
        AuthenticatedUserDTO authenticatedUserDTO = toAuthenticatedUserDTO(serviceToken, user);

        return buildSuccessApiResponse(authenticatedUserDTO);
    }

    @PostMapping(path = "/login/jwt",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ApiResponse<AuthenticatedUserDTO> loginViaJWT(
            @RequestAttribute(USER_ID_ATTRIBUTE_NAME) UUID userID,
            @RequestAttribute(TOKEN_ATTRIBUTE_NAME) String jwt
    ) throws UserNotFoundException, PermissionException, UserIsDeactivatedException {
        // Validar que JWT pertenece al usuario que intenta acceder
        JwtData jwtData = jwtGenerator.extractData(jwt);
        if (!authUtils.doUsersMatch(userID, jwtData.getUserID())) {
            throw new PermissionException();
        }

        // Iniciar sesión en servicio
        User user = authService.loginViaJWT(userID);

        // Regenerar token para usaurio
        String serviceToken = authUtils.generateJWTFromUser(user);
        AuthenticatedUserDTO authenticatedUserDTO = toAuthenticatedUserDTO(serviceToken, user);

        return buildSuccessApiResponse(authenticatedUserDTO);
    }

    @PatchMapping(path = "/{userID}/password",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> updatePassword(
            @RequestAttribute(USER_ID_ATTRIBUTE_NAME) UUID userID,
            @PathVariable("userID") UUID pathUserID,
            @Validated @RequestBody ChangePasswordParamsDTO params
    ) throws PermissionException, UserNotFoundException, PasswordsDoNotMatchException {
        // Comprobar que usuario actual y usuario objetivo son el mismo
        if (!authUtils.doUsersMatch(userID, pathUserID)) {
            throw new PermissionException();
        }

        // Actualizar contraseña en el servicio
        authService.changePassword(userID, params.getOldPassword(), params.getNewPassword());

        return buildEmptySuccessApiResponse();
    }

}
