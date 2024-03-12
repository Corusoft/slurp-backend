package dev.corusoft.slurp.common.api.error;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ServiceException extends ApiErrorDetails {

    public ServiceException(String reason) {
        super(reason);
    }

}
