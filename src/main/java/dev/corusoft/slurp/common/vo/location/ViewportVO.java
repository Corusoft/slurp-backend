package dev.corusoft.slurp.common.vo.location;

import lombok.Data;

@Data
public class ViewportVO {
    private final LocationVO northEast;
    private final LocationVO southWest;
}
