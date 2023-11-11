package dev.corusoft.slurp.common.api;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ApiResponse<T> {
    private final boolean success;

    private final LocalDateTime timestamp;

    private final T data;
}
