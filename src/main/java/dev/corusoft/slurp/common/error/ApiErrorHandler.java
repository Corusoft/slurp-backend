package dev.corusoft.slurp.common.error;

import dev.corusoft.slurp.common.i18n.Translator;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.time.LocalDateTime;

@ControllerAdvice
public class ApiErrorHandler {
    /* ******************** TRADUCCIONES DE EXCEPCIONES ******************** */
    private final Translator translator;

    public ApiErrorHandler(Translator translator) {
        this.translator = translator;
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


}
