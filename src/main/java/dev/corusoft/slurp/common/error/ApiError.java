package dev.corusoft.slurp.common.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ApiError {

    private HttpStatus status;

    private LocalDateTime timestamp;

    private String message;

    private String debugMessage;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ApiErrorDetails> errors;
}
