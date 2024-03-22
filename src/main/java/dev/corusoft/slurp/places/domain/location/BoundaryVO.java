package dev.corusoft.slurp.places.domain.location;

import lombok.Data;

@Data
public class BoundaryVO {
    private final LocationVO location;
    private final ViewportVO viewport;
}
