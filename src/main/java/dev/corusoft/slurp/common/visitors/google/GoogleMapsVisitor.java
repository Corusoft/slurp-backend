package dev.corusoft.slurp.common.visitors.google;

import com.google.maps.model.PlacesSearchResult;
import dev.corusoft.slurp.places.domain.Candidate;

public interface GoogleMapsVisitor {
    Candidate visit(PlacesSearchResult searchResult);
}
