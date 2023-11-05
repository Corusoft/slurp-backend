package dev.corusoft.slurp.users.infrastructure.controllers;

import dev.corusoft.slurp.common.error.ApiError;
import dev.corusoft.slurp.common.i18n.Translator;
import dev.corusoft.slurp.users.domain.exceptions.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

import static dev.corusoft.slurp.common.error.ApiErrorHandler.buildApiError;

@ControllerAdvice(assignableTypes = {AuthController.class})
public class AuthApiErrorHandler {
    /* ******************** TRADUCCIONES DE EXCEPCIONES ******************** */
    public static final String USER_ALREADY_EXISTS_KEY = "UserAlreadyExistsException";

    private final Translator translator;

    public AuthApiErrorHandler(Translator translator) {
        this.translator = translator;
    }


    /* ******************** MANEJADORES DE EXCEPCIONES ******************** */
    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)     //400
    @ResponseBody
    public ApiError handleUserAlreayExistsException(UserAlreadyExistsException exception, Locale locale) {
        String translatedMessage = translator.generateMessage(exception.getKey().toString(), null, locale);
        Object[] args = {translatedMessage, exception.getKey().toString()};
        String errorMessage = translator.generateMessage(USER_ALREADY_EXISTS_KEY, args, locale);

        return buildApiError(HttpStatus.BAD_REQUEST, errorMessage, exception);
    }

}
