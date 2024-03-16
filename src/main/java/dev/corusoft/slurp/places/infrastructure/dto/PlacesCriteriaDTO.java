package dev.corusoft.slurp.places.infrastructure.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    private Double latitude;

    @NotNull
    @Range(min = -180, max = 180)
    private Double longitude;

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
