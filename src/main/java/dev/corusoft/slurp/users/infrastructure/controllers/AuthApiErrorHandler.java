package dev.corusoft.slurp.users.infrastructure.controllers;

import dev.corusoft.slurp.common.api.ApiResponse;
import dev.corusoft.slurp.common.api.ErrorApiResponseBody;
import dev.corusoft.slurp.common.i18n.Translator;
import dev.corusoft.slurp.users.domain.exceptions.IncorrectLoginException;
import dev.corusoft.slurp.users.domain.exceptions.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Locale;

import static dev.corusoft.slurp.common.api.ApiResponseHelper.buildErrorApiResponse;

@ControllerAdvice(assignableTypes = {AuthController.class})
public class AuthApiErrorHandler {
    /* ******************** TRADUCCIONES DE EXCEPCIONES ******************** */
    public static final String USER_ALREADY_EXISTS_KEY = "UserAlreadyExistsException";
    public static final String INCORRECT_LOGIN_KEY = "IncorrectLoginException";


    private final Translator translator;

    public AuthApiErrorHandler(Translator translator) {
        this.translator = translator;
    }


    /* ******************** MANEJADORES DE EXCEPCIONES ******************** */
    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)     //400
    @ResponseBody
    public ApiResponse<ErrorApiResponseBody> handleUserAlreayExistsException(UserAlreadyExistsException exception, Locale locale) {
        String translatedMessage = translator.generateMessage(exception.getKey().toString(), locale);
        Object[] args = {translatedMessage, exception.getKey().toString()};
        String errorMessage = translator.generateMessage(USER_ALREADY_EXISTS_KEY, args, locale);

        return buildErrorApiResponse(HttpStatus.BAD_REQUEST, errorMessage, exception);
    }

    @ExceptionHandler(IncorrectLoginException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)     //400
    @ResponseBody
    public ApiResponse<ErrorApiResponseBody> handleIncorrectLoginException(IncorrectLoginException exception, Locale locale) {
        String errorMessage = translator.generateMessage(INCORRECT_LOGIN_KEY, locale);

        return buildErrorApiResponse(HttpStatus.BAD_REQUEST, errorMessage, exception);
    }

}
