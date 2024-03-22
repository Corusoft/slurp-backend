package dev.corusoft.slurp.common.api;

import dev.corusoft.slurp.common.api.error.ApiErrorDetails;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponseHelper {

    private static <T> ApiResponse<T> createResponse(boolean success, T body) {
        ApiResponse.ApiResponseBuilder<T> responseBuilder = ApiResponse.<T>builder()
                .success(success)
                .data(body)
                .timestamp(LocalDateTime.now());

        return responseBuilder.build();
    }

    private static <E extends Exception> ErrorApiResponseBody generateErrorResponseBody(HttpStatus status, String message, E exception) {
        return generateErrorResponseBody(status, message, exception, null);
    }

    private static <E extends ApiErrorDetails> ErrorApiResponseBody generateErrorResponseBody(HttpStatus status, String message, List<E> errors) {
        return generateErrorResponseBody(status, message, null, errors);
    }

    private static <E extends Exception> ErrorApiResponseBody generateErrorResponseBody(HttpStatus status, String message, E exception, List<? extends ApiErrorDetails> errors) {
        String debugMessage = (exception != null) ? exception.getLocalizedMessage() : null;
        boolean hasErrors = (errors != null) && (!errors.isEmpty());
        List<? extends ApiErrorDetails> errorDetails = hasErrors ? errors : null;

        return ErrorApiResponseBody.builder()
                .status(status.name())
                .statusCode(status.value())
                .message(message)
                .debugMessage(debugMessage)
                .errors(errorDetails)
                .build();
    }


    /* ******************** SUCCESS ******************** */
    public static ApiResponse<Void> buildEmptySuccessApiResponse() {
        return createResponse(true, null);
    }

    public static <T> ApiResponse<T> buildSuccessApiResponse(T content) {
        return createResponse(true, content);
    }

    /* ******************** ERRORS ******************** */
    public static ApiResponse<ErrorApiResponseBody> buildErrorApiResponse(HttpStatus status, String message) {
        return buildErrorApiResponse(status, message, null);
    }

    public static ApiResponse<ErrorApiResponseBody> buildErrorApiResponse(HttpStatus status, String message, Exception exception) {
        ErrorApiResponseBody body = generateErrorResponseBody(status, message, exception);

        return createResponse(false, body);
    }

    public static ApiResponse<ErrorApiResponseBody> buildErrorApiResponse(HttpStatus status, String message, Exception exception, List<? extends ApiErrorDetails> errors) {
        ErrorApiResponseBody body = generateErrorResponseBody(status, message, exception, errors);

        return createResponse(false, body);
    }
}
