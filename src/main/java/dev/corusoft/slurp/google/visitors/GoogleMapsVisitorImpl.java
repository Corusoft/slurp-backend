package dev.corusoft.slurp.google.visitors;

import com.google.maps.model.PlacesSearchResult;
import dev.corusoft.slurp.places.domain.CandidateSummary;
import dev.corusoft.slurp.places.domain.location.LocationVO;

public class GoogleMapsVisitorImpl implements GoogleMapsVisitor {

    @Override
    public CandidateSummary visit(PlacesSearchResult searchResult) {
        LocationVO location = new LocationVO(
                searchResult.geometry.location.lng,
                searchResult.geometry.location.lat
        );

        CandidateSummary.CandidateSummaryBuilder builder = CandidateSummary.builder()
                .placeId(searchResult.placeId)
                .name(searchResult.name)
                .location(location)
                .types(searchResult.types)
                .formattedAddress(searchResult.formattedAddress);

        return builder.build();
    }


}
