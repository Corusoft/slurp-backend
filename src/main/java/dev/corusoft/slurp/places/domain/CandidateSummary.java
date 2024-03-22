package dev.corusoft.slurp.places.domain;

import dev.corusoft.slurp.places.domain.location.DistanceVO;
import dev.corusoft.slurp.places.domain.location.LocationVO;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateSummary {
    private String placeId;
    private String name;
    private LocationVO location;
    private String formattedAddress;
    private String[] types;
    private DistanceVO distance;
}
