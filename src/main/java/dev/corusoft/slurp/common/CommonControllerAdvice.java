package dev.corusoft.slurp.common;

import dev.corusoft.slurp.common.api.ApiResponse;
import dev.corusoft.slurp.common.api.ErrorApiResponseBody;
import dev.corusoft.slurp.common.api.error.ApiValidationErrorDetails;
import dev.corusoft.slurp.common.exception.InvalidArgumentException;
import dev.corusoft.slurp.common.exception.MissingMandatoryValueException;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

import static dev.corusoft.slurp.common.api.ApiResponseHelper.buildErrorApiResponse;

@ControllerAdvice
@NoArgsConstructor
public class CommonControllerAdvice {
    /* ******************** TRADUCCIONES DE EXCEPCIONES ******************** */
    public static final String METHOD_ARGUMENT_NOT_VALID_KEY = "MethodArgumentNotValidException";
    public static final String VALIDATION_KEY = "ValidationException";
    public static final String INVALID_ARGUMENT_KEY = "InvalidArgumentException";
    public static final String MISSING_MANDATORY_VALUE_KEY = "MissingMandatoryValueException";


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

    @ExceptionHandler(InvalidArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiResponse<ErrorApiResponseBody> handleInvalidArgumentException(InvalidArgumentException exception, Locale locale) {
        Object[] args = {exception.getValue(), exception.getObjectName(), exception.getField()};
        String errorMessage = Translator.generateMessage(INVALID_ARGUMENT_KEY, args, locale);

        return buildErrorApiResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(MissingMandatoryValueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiResponse<ErrorApiResponseBody> handleMissingMandatoryValueException(MissingMandatoryValueException exception, Locale locale) {
        //String translatedMessage = Translator.generateMessage(MISSING_MANDATORY_VALUE_KEY, locale);
        Object[] args = {exception.getField()};
        String errorMessage = Translator.generateMessage(MISSING_MANDATORY_VALUE_KEY, args, locale);

        return buildErrorApiResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

}
