package dev.corusoft.slurp.places.application;

import com.google.maps.model.*;
import dev.corusoft.slurp.common.pagination.Block;
import dev.corusoft.slurp.google.visitors.GoogleMapsVisitor;
import dev.corusoft.slurp.google.visitors.GoogleMapsVisitorImpl;
import dev.corusoft.slurp.places.application.criteria.PlacesCriteria;
import dev.corusoft.slurp.places.application.utils.PlacesDistanceCalculator;
import dev.corusoft.slurp.places.domain.CandidateSummary;
import dev.corusoft.slurp.places.domain.location.DistanceVO;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class PlacesServiceImpl implements PlacesService {
    /* DEPENDENCIES */
    private final GoogleMapsVisitor gMapsVisitor;
    private final GoogleApiService googleApi;

    public PlacesServiceImpl(GoogleApiService googleApi) {
        this.gMapsVisitor = new GoogleMapsVisitorImpl();
        this.googleApi = googleApi;
    }

    /* USE CASES */

    @Override
    public Block<CandidateSummary> findCandidatesNearby(PlacesCriteria criteria) {
        PlacesSearchResponse apiResponse = googleApi.findNearbyPlaces(criteria);
        // Realizar petición y gestionar errores
        if (apiResponse.results == null) {
            return Block.emptyBlock();
        }

        // Convertir resultados recibidos
        LatLng currentPosition = new LatLng(criteria.getLatitude(), criteria.getLongitude());
        List<CandidateSummary> candidateSummaries = new ArrayList<>();
        for (PlacesSearchResult result : apiResponse.results) {
            CandidateSummary candidateSummary = createCandidateSummary(result, currentPosition);
            candidateSummaries.add(candidateSummary);
        }

        return new Block<>(candidateSummaries, apiResponse.nextPageToken);
    }

    private CandidateSummary createCandidateSummary(PlacesSearchResult result, LatLng currentPosition) {
        CandidateSummary candidateSummary = gMapsVisitor.visit(result);
        DistanceVO distance = PlacesDistanceCalculator.calculateDistance(candidateSummary, currentPosition);
        candidateSummary.setDistance(distance);
        return candidateSummary;
    }

}
