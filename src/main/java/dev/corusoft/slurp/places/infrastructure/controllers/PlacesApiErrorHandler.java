package dev.corusoft.slurp.places.infrastructure.controllers;

import dev.corusoft.slurp.common.Translator;
import dev.corusoft.slurp.common.api.ApiResponse;
import dev.corusoft.slurp.common.api.ErrorApiResponseBody;
import dev.corusoft.slurp.common.api.error.ServiceException;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

import static dev.corusoft.slurp.common.api.ApiResponseHelper.buildErrorApiResponse;

@ControllerAdvice(assignableTypes = {PlacesController.class})
@NoArgsConstructor
public class PlacesApiErrorHandler {
    /* ******************** TRADUCCIONES DE EXCEPCIONES ******************** */
    public static final String SERVICE_KEY = "ServiceException";

    /* ******************** MANEJADORES DE EXCEPCIONES ******************** */
    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)   //500
    @ResponseBody
    public ApiResponse<ErrorApiResponseBody> handleServiceException(ServiceException exception, Locale locale) {
        String errorMessage = Translator.generateMessage(SERVICE_KEY, locale);

        return buildErrorApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage, exception);
    }
}
