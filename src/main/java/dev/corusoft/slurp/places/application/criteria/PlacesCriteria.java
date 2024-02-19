package dev.corusoft.slurp.places.application.criteria;

import com.google.maps.model.PlaceType;
import com.google.maps.model.PriceLevel;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
@Builder
public class PlacesCriteria {
    @NotNull
    @Range(min = -90, max = 90)
    private final double latitude;

    @NotNull
    @Range(min = -180, max = 180)
    private final double longitude;

    @Positive
    @Max(value = 50000)
    private final Integer radius;

    private final PlaceType placeType;

    private final String keywords;

    private final PriceLevel maxPriceLevel;

    private final PriceLevel minPriceLevel;

    private final Boolean isOpenNow;
}
