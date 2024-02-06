package dev.corusoft.slurp.places.domain.location;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class DistanceVO {
    private BigDecimal distance;
    private DistanceVOUnits unit;

    public enum DistanceVOUnits {
        KILOMETER, METER
    }
}
