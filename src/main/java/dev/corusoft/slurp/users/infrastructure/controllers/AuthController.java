package dev.corusoft.slurp.users.infrastructure.controllers;

import dev.corusoft.slurp.common.exception.EntityAlreadyExistsException;
import dev.corusoft.slurp.common.security.jwt.application.JwtGenerator;
import dev.corusoft.slurp.common.security.jwt.domain.JwtData;
import dev.corusoft.slurp.users.application.AuthService;
import dev.corusoft.slurp.users.domain.User;
import dev.corusoft.slurp.users.infrastructure.dto.input.RegisterUserParamsDTO;
import dev.corusoft.slurp.users.infrastructure.dto.output.AuthenticatedUserDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

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

    /* ******************** I18N ******************** */


    /* ******************** EXCEPCIONES ******************** */


    /* ******************** ENDPOINTS ******************** */

    @PostMapping(path = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<AuthenticatedUserDTO> register(
            @Validated @RequestBody RegisterUserParamsDTO params
    ) throws EntityAlreadyExistsException {
        // Registrar usuario
        User registeredUser = authService.register(params);

        // Generar contenido de respuesta
        URI resourceLocation = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/v1/users/{userID}")
                .buildAndExpand(registeredUser.getUserID())
                .toUri();
        String serviceToken = generateJWTFromUser(registeredUser);
        AuthenticatedUserDTO authenticatedUserDTO = toAuthenticatedUserDTO(serviceToken, registeredUser);

        // Crear respuesta HTTP y enviar
        return ResponseEntity
                .created(resourceLocation)
                .contentType(MediaType.APPLICATION_JSON)
                .body(authenticatedUserDTO);
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
