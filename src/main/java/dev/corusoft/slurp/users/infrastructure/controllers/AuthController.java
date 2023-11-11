package dev.corusoft.slurp.users.infrastructure.controllers;

import dev.corusoft.slurp.common.api.ApiResponse;
import dev.corusoft.slurp.common.security.jwt.application.JwtGenerator;
import dev.corusoft.slurp.common.security.jwt.domain.JwtData;
import dev.corusoft.slurp.users.application.AuthService;
import dev.corusoft.slurp.users.domain.User;
import dev.corusoft.slurp.users.domain.exceptions.IncorrectLoginException;
import dev.corusoft.slurp.users.domain.exceptions.UserAlreadyExistsException;
import dev.corusoft.slurp.users.infrastructure.dto.input.LoginParamsDTO;
import dev.corusoft.slurp.users.infrastructure.dto.input.RegisterUserParamsDTO;
import dev.corusoft.slurp.users.infrastructure.dto.output.AuthenticatedUserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static dev.corusoft.slurp.common.api.ApiResponseHelper.buildSuccessApiResponse;
import static dev.corusoft.slurp.users.infrastructure.dto.conversors.UserConversor.toAuthenticatedUserDTO;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {
    /* ******************** DEPENDENCIAS ******************** */
    private final AuthService authService;
    private final JwtGenerator jwtGenerator;


    public AuthController(AuthService authService, JwtGenerator jwtGenerator) {
        this.authService = authService;
        this.jwtGenerator = jwtGenerator;
    }


    /* ******************** ENDPOINTS ******************** */

    @PostMapping(path = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
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
        String serviceToken = generateJWTFromUser(registeredUser);
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
    @ResponseBody
    public ApiResponse<AuthenticatedUserDTO> login(
            @Validated @RequestBody LoginParamsDTO params
    ) throws IncorrectLoginException {
        // Iniciar sesión en servicio
        User user = authService.login(params.getNickname(), params.getPassword());

        // Generar token para usaurio
        String serviceToken = generateJWTFromUser(user);
        AuthenticatedUserDTO authenticatedUserDTO = toAuthenticatedUserDTO(serviceToken, user);

        return buildSuccessApiResponse(authenticatedUserDTO);
    }


    /* ******************** HELPER FUNCTIONS ******************** */
    public String generateJWTFromUser(User user) {
        String nickname = user.getCredential().getNickname();
        List<String> roles = user.getAttachedRoles()
                .stream()
                .map(Enum::name)
                .toList();

        JwtData jwtData = JwtData.builder()
                .userID(user.getUserID())
                .nickname(nickname)
                .roles(roles)
                .build();

        return jwtGenerator.generateJWT(jwtData);
    }
}
