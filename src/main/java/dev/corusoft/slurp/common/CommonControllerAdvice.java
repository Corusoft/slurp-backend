package dev.corusoft.slurp.common;

import dev.corusoft.slurp.common.api.ApiResponse;
import dev.corusoft.slurp.common.api.ErrorApiResponseBody;
import dev.corusoft.slurp.common.api.error.ApiValidationErrorDetails;
import jakarta.validation.ValidationException;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Locale;

import static dev.corusoft.slurp.common.api.ApiResponseHelper.buildErrorApiResponse;

@ControllerAdvice
@NoArgsConstructor
public class CommonControllerAdvice {
    /* ******************** TRADUCCIONES DE EXCEPCIONES ******************** */
    public static final String METHOD_ARGUMENT_NOT_VALID_KEY = "MethodArgumentNotValidException";
    public static final String VALIDATION_KEY = "ValidationException";


    /* ******************** MANEJADORES DE EXCEPCIONES ******************** */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiResponse<ErrorApiResponseBody> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, Locale locale) {
        List<ApiValidationErrorDetails> errorsList = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map((field -> new ApiValidationErrorDetails(
                        field.getObjectName(),
                        field.getField(),
                        field.getRejectedValue(),
                        field.getDefaultMessage()))
                ).toList();

        String errorMessage = Translator.generateMessage(METHOD_ARGUMENT_NOT_VALID_KEY, locale);

        return buildErrorApiResponse(HttpStatus.BAD_REQUEST, errorMessage, null, errorsList);
    }


    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiResponse<ErrorApiResponseBody> handleValidationException(ValidationException exception, Locale locale) {
        String errorMessage = Translator.generateMessage(VALIDATION_KEY, locale);

        return buildErrorApiResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

}
