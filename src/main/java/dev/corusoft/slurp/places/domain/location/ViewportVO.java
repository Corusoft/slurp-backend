package dev.corusoft.slurp.places.domain.location;

import lombok.Data;

@Data
public class ViewportVO {
    private final LocationVO northEast;
    private final LocationVO southWest;
}
