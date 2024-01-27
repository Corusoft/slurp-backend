package dev.corusoft.slurp.common.visitors.google;

import com.google.maps.model.PlacesSearchResult;
import dev.corusoft.slurp.places.domain.Candidate;
import dev.corusoft.slurp.places.domain.location.LocationVO;

public class GoogleMapsVisitorImpl implements GoogleMapsVisitor {

    @Override
    public Candidate visit(PlacesSearchResult searchResult) {
        LocationVO location = new LocationVO(
                searchResult.geometry.location.lng,
                searchResult.geometry.location.lat
        );

        Candidate.CandidateBuilder builder = Candidate.builder()
                .placeId(searchResult.placeId)
                .name(searchResult.name)
                .location(location)
                .types(searchResult.types)
                .formattedAddress(searchResult.formattedAddress);

        return builder.build();
    }


}
