package dev.corusoft.slurp.users.infrastructure.controllers;

import dev.corusoft.slurp.common.api.ApiResponse;
import dev.corusoft.slurp.common.api.ErrorApiResponseBody;
import dev.corusoft.slurp.common.exception.PermissionException;
import dev.corusoft.slurp.common.i18n.Translator;
import dev.corusoft.slurp.users.domain.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

import static dev.corusoft.slurp.common.api.ApiResponseHelper.buildErrorApiResponse;

@ControllerAdvice(assignableTypes = {AuthController.class, UserController.class})
public class UsersApiErrorHandler {
    /* ******************** TRADUCCIONES DE EXCEPCIONES ******************** */
    public static final String USER_ALREADY_EXISTS_KEY = "UserAlreadyExistsException";
    public static final String INCORRECT_LOGIN_KEY = "IncorrectLoginException";
    public static final String USER_NOT_FOUND_KEY = "UserNotFoundException";
    public static final String PASSWORD_DO_NOT_MATCH_KEY = "PasswordsDoNotMatchException";
    public static final String PERMISSION_KEY = "PermissionException";


    private final Translator translator;

    public UsersApiErrorHandler(Translator translator) {
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

        return buildErrorApiResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)     //404
    @ResponseBody
    public ApiResponse<ErrorApiResponseBody> handleUserNotFoundException(UserNotFoundException exception, Locale locale) {
        String errorMessage = translator.generateMessage(USER_NOT_FOUND_KEY, locale);

        return buildErrorApiResponse(HttpStatus.NOT_FOUND, errorMessage);
    }

    @ExceptionHandler(PasswordsDoNotMatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)     //400
    @ResponseBody
    public ApiResponse<ErrorApiResponseBody> handlePasswordsDoNotMatchException(PasswordsDoNotMatchException exception, Locale locale) {
        String errorMessage = translator.generateMessage(PASSWORD_DO_NOT_MATCH_KEY, locale);

        return buildErrorApiResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(PermissionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)     //403
    @ResponseBody
    public ApiResponse<ErrorApiResponseBody> handlePermissionException(PermissionException exception, Locale locale) {
        String errorMessage = translator.generateMessage(PERMISSION_KEY, locale);

        return buildErrorApiResponse(HttpStatus.FORBIDDEN, errorMessage);
    }
}
