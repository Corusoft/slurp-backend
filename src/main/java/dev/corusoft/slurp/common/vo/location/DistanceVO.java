package dev.corusoft.slurp.common.vo.location;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DistanceVO {
    private BigDecimal distance;
    private DistanceVOUnits unit;

    public enum DistanceVOUnits {
        KILOMETER, METER
    }
}
