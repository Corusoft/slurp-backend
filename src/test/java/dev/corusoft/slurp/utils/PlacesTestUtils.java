package dev.corusoft.slurp.utils;

import com.google.maps.model.*;
import dev.corusoft.slurp.common.pagination.Block;
import dev.corusoft.slurp.google.visitors.GoogleMapsVisitor;
import dev.corusoft.slurp.google.visitors.GoogleMapsVisitorImpl;
import dev.corusoft.slurp.places.application.utils.PlacesDistanceCalculator;
import dev.corusoft.slurp.places.domain.CandidateSummary;
import dev.corusoft.slurp.places.domain.location.DistanceVO;

import java.util.List;
import java.util.stream.Stream;

public class PlacesTestUtils {
    /* ************************* DEPENDENCIAS ************************* */
    private static final GoogleMapsVisitor gMapsVisitor = new GoogleMapsVisitorImpl();


    /* ************************* MÉTODOS AUXILIARES ************************* */
    public static CandidateSummary createCandidateSummary(PlacesSearchResult result, LatLng currentPosition) {
        CandidateSummary candidateSummary = gMapsVisitor.visit(result);
        DistanceVO distance = PlacesDistanceCalculator.calculateDistance(candidateSummary, currentPosition);
        candidateSummary.setDistance(distance);

        return candidateSummary;
    }

    public static Block<CandidateSummary> createBlockOfCandidateSummary(PlacesSearchResponse response, LatLng currentPosition) {
        if (response.results.length == 0)
            return Block.emptyBlock();

        List<CandidateSummary> candidates = Stream.of(response.results)
                .map(result -> createCandidateSummary(result, currentPosition))
                .toList();

        return new Block<>(candidates, response.nextPageToken);
    }

}
