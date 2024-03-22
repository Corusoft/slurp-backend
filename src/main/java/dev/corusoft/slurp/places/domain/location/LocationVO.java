package dev.corusoft.slurp.places.domain.location;

import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class LocationVO {
    private final BigDecimal longitude;
    private final BigDecimal latitude;

    public LocationVO(double longitude, double latitude) {
        int AMMOUNT_OF_LOCATION_DECIMALS = 5;           // Aporta precisión aproximada e 1 metro

        this.longitude = BigDecimal.valueOf(longitude)
                .setScale(AMMOUNT_OF_LOCATION_DECIMALS, RoundingMode.DOWN);
        this.latitude = BigDecimal.valueOf(latitude)
                .setScale(AMMOUNT_OF_LOCATION_DECIMALS, RoundingMode.DOWN);
    }
}
