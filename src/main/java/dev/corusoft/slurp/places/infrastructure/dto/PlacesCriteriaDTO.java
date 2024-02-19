package dev.corusoft.slurp.places.infrastructure.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlacesCriteriaDTO {
    @NotNull
    @Range(min = -90, max = 90)
    private double latitude;

    @NotNull
    @Range(min = -180, max = 180)
    private double longitude;

    @Positive
    @Max(value = 50000)
    private Integer radius;

    @NotEmpty
    private String placeType;

    private String keywords;

    @Range(min = 1, max = 5)
    private String maxPriceLevel;        // com.google.maps.model.PriceLevel

    @Range(min = 1, max = 5)
    private String minPriceLevel;        // com.google.maps.model.PriceLevel

    private Boolean isOpenNow;
}
