package dev.corusoft.slurp.common.visitors.google;

import com.google.maps.model.PlacesSearchResult;
import dev.corusoft.slurp.places.domain.Candidate;

public class GoogleMapsVisitorClient {
    private final GoogleMapsVisitor gMapsVisitor;


    public GoogleMapsVisitorClient() {
        this.gMapsVisitor = new GoogleMapsVisitorImpl();
    }

    public GoogleMapsVisitorClient(GoogleMapsVisitor gMapsVisitor) {
        this.gMapsVisitor = gMapsVisitor;
    }

    public Candidate doVisit(PlacesSearchResult object) {
        return this.doVisit(object, gMapsVisitor);
    }

    public Candidate doVisit(PlacesSearchResult object, GoogleMapsVisitor visitor) {
        return visitor.visit(object);
    }
}
