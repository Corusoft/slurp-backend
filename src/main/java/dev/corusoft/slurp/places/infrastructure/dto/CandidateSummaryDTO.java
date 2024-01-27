package dev.corusoft.slurp.places.infrastructure.dto;

import dev.corusoft.slurp.places.domain.location.DistanceVO;
import dev.corusoft.slurp.places.domain.location.LocationVO;
import jakarta.validation.constraints.*;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateSummaryDTO {
    @NotBlank
    private String placeId;

    @NotBlank
    private String name;

    @NotNull
    private LocationVO location;

    private String formattedAddress;

    @NotEmpty
    private String[] types;

    @PositiveOrZero
    private DistanceVO distance;
}
