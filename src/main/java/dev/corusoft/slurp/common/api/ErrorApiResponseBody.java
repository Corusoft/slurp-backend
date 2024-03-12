package dev.corusoft.slurp.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.corusoft.slurp.common.api.error.ApiErrorDetails;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ErrorApiResponseBody implements ApiResponseBody {
    private final int statusCode;

    private final String status;

    private final String message;

    private final String debugMessage;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<? extends ApiErrorDetails> errors;
}
