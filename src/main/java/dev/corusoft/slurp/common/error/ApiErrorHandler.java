package dev.corusoft.slurp.common.error;

import dev.corusoft.slurp.common.exception.*;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Locale;

@ControllerAdvice
public class ApiErrorHandler {
    /* ******************** TRADUCCIONES DE EXCEPCIONES ******************** */
    public static final String ENTITY_ALREADY_EXISTS_KEY = "EntityAlreadyExistsException";
    public static final String ENTITY_NOT_FOUND_KEY = "EntityNotFoundException";
    public static final String PERMISSION_KEY = "PermissionException";
    private final MessageSource messageSource;

    public ApiErrorHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public static ApiError buildApiError(HttpStatus status, String message, Throwable exception) {
        return ApiError.builder()
                .status(status)
                .timestamp(LocalDateTime.now())
                .message(message)
                .debugMessage(exception.getLocalizedMessage())
                .build();
    }

    /* ******************** MANEJADORES DE EXCEPCIONES ******************** */
    @ExceptionHandler(EntityAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)     // 400
    @ResponseBody
    public ApiError handleEntityAlreadyExistsException(EntityAlreadyExistsException exception, Locale locale) {
        String translatedMessage = generateErrorMessage(exception.getEntityName(), null, locale);
        Object[] args = {translatedMessage, exception.getKey().toString()};
        String errorMessage = generateErrorMessage(ENTITY_ALREADY_EXISTS_KEY, args, locale);

        return buildApiError(HttpStatus.BAD_REQUEST, errorMessage, exception);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)     // 400
    @ResponseBody
    public ApiError handleEntityNotFoundException(EntityNotFoundException exception, Locale locale) {
        String translatedMessage = generateErrorMessage(exception.getEntityName(), null, locale);
        Object[] args = {translatedMessage, exception.getKey().toString()};
        String errorMessage = generateErrorMessage(ENTITY_NOT_FOUND_KEY, args, locale);

        return buildApiError(HttpStatus.NOT_FOUND, errorMessage, exception);
    }

    @ExceptionHandler(PermissionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)     // 403
    @ResponseBody
    public ApiError handlePermissionException(PermissionException exception, Locale locale) {
        String errorMessage = generateErrorMessage(PERMISSION_KEY, null, PERMISSION_KEY, locale);

        return buildApiError(HttpStatus.FORBIDDEN, errorMessage, exception);
    }

    /* Helper methods */
    public String generateErrorMessage(String exceptionKey, Locale locale) {
        return generateErrorMessage(exceptionKey, null, locale);
    }

    public String generateErrorMessage(String exceptionKey, Object[] args, Locale locale) {
        return messageSource.getMessage(
                exceptionKey,
                args,
                exceptionKey,
                locale
        );
    }

    public String generateErrorMessage(String exceptionKey, Object[] args, String defaultMessage, Locale locale) {
        return messageSource.getMessage(
                exceptionKey,
                args,
                defaultMessage,
                locale
        );
    }
}
