package dev.corusoft.slurp.google.visitors;

import com.google.maps.model.PlacesSearchResult;
import dev.corusoft.slurp.places.domain.CandidateSummary;

public interface GoogleMapsVisitor {
    CandidateSummary visit(PlacesSearchResult searchResult);
}
