package dev.corusoft.slurp.places.domain.location;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class DistanceVO {
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
    private BigDecimal distance;
    private DistanceVOUnits unit;

    public enum DistanceVOUnits {
        KILOMETER, METER
    }
}
