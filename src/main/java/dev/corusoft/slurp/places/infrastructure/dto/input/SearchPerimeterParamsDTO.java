package dev.corusoft.slurp.places.infrastructure.dto.input;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchPerimeterParamsDTO {
    @NotNull
    @Positive
    private double radius;

    @NotNull
    private double longitude;

    @NotNull
    private double latitude;
}
