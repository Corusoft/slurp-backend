package dev.corusoft.slurp.common.error;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

public abstract class BaseApiError {

    private HttpStatus status;

    private LocalDateTime timestamp;

    private String message;

    private String debugMessage;

    private List<ApiError> errors;
}
