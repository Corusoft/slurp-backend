package dev.corusoft.slurp.utils;

import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResult;
import dev.corusoft.slurp.google.visitors.GoogleMapsVisitor;
import dev.corusoft.slurp.google.visitors.GoogleMapsVisitorImpl;
import dev.corusoft.slurp.places.application.utils.PlacesDistanceCalculator;
import dev.corusoft.slurp.places.domain.CandidateSummary;
import dev.corusoft.slurp.places.domain.location.DistanceVO;
import org.springframework.stereotype.Component;

@Component
public class PlacesTestUtils {
    /* ************************* DEPENDENCIAS ************************* */
    private final GoogleMapsVisitor gMapsVisitor = new GoogleMapsVisitorImpl();


    /* ************************* MÉTODOS AUXILIARES ************************* */
    public CandidateSummary createCandidateSummary(PlacesSearchResult result, LatLng currentPosition) {
        CandidateSummary candidateSummary = gMapsVisitor.visit(result);
        DistanceVO distance = PlacesDistanceCalculator.calculateDistance(candidateSummary, currentPosition);
        candidateSummary.setDistance(distance);

        return candidateSummary;
    }

}
