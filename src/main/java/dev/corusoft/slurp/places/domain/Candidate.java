package dev.corusoft.slurp.places.domain;

import dev.corusoft.slurp.common.vo.location.DistanceVO;
import dev.corusoft.slurp.common.vo.location.LocationVO;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Candidate {
    private String placeId;
    private String name;
    private LocationVO location;
    private String formattedAddress;
    private String[] types;
    private DistanceVO distance;
}
