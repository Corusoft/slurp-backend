package dev.corusoft.slurp.places.application;

import com.google.maps.*;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;
import dev.corusoft.slurp.common.pagination.Block;
import dev.corusoft.slurp.google.visitors.GoogleMapsVisitor;
import dev.corusoft.slurp.google.visitors.GoogleMapsVisitorImpl;
import dev.corusoft.slurp.places.application.criteria.PlacesCriteria;
import dev.corusoft.slurp.places.domain.CandidateSummary;
import dev.corusoft.slurp.places.domain.location.DistanceVO;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Transactional
@Service
public class PlacesServiceImpl implements PlacesService {
    /* DEPENDENCIES */
    private final GeoApiContext context;
    private final GoogleMapsVisitor gMapsVisitor;

    public PlacesServiceImpl(GeoApiContext context) {
        this.context = context;
        this.gMapsVisitor = new GoogleMapsVisitorImpl();
    }

    /* USE CASES */

    private static PlacesSearchResponse executeRequest(NearbySearchRequest request) {
        PlacesSearchResponse response = null;
        try {
            response = request.await();
        } catch (ApiException e) {
            log.error("ApiException: ", e);
        } catch (InterruptedException e) {
            log.error("InterruptedException: ", e);
        } catch (IOException e) {
            log.error("IOException: ", e);
        }

        return response;
    }

    @Override
    public Block<CandidateSummary> findCandidatesNearby(PlacesCriteria criteria) {
        // Configurar petición
        LatLng currentPosition = new LatLng(criteria.getLatitude(), criteria.getLongitude());
        NearbySearchRequest request = PlacesApi.nearbySearchQuery(context, currentPosition);
        configureRequestUsingCriteria(request, criteria);

        // Realizar petición y gestionar errores
        PlacesSearchResponse apiResponse = executeRequest(request);
        if (apiResponse.results == null) {
            return Block.emptyBlock();
        }

        // Convertir resultados recibidos
        List<CandidateSummary> candidateSummaries = new ArrayList<>();
        for (PlacesSearchResult result : apiResponse.results) {
            CandidateSummary candidateSummary = createCandidateSummary(result, currentPosition);
            candidateSummaries.add(candidateSummary);
        }

        return new Block<>(candidateSummaries, apiResponse.nextPageToken);
    }

    private CandidateSummary createCandidateSummary(PlacesSearchResult result, LatLng currentPosition) {
        CandidateSummary candidateSummary = gMapsVisitor.visit(result);
        DistanceVO distance = calculateDistance(candidateSummary, currentPosition);
        candidateSummary.setDistance(distance);
        return candidateSummary;
    }

    /* HELPER FUNCTIONS */
    private void configureRequestUsingCriteria(NearbySearchRequest request, PlacesCriteria criteria) {
        if (criteria.getRadius() != null) request.radius(criteria.getRadius());
        if (criteria.getPlaceType() != null) request.type(criteria.getPlaceType());
        if (criteria.getKeywords() != null) request.keyword(criteria.getKeywords());
        if (criteria.getMinPriceLevel() != null) request.minPrice(criteria.getMinPriceLevel());
        if (criteria.getMaxPriceLevel() != null) request.maxPrice(criteria.getMaxPriceLevel());
        if (criteria.getIsOpenNow() != null) request.openNow(criteria.getIsOpenNow());
    }

    private DistanceVO calculateDistance(CandidateSummary candidateSummary, LatLng currentLocation) {

        com.javadocmd.simplelatlng.LatLng candidateLatLng = new com.javadocmd.simplelatlng.LatLng(
                candidateSummary.getLocation().getLatitude().doubleValue(),
                candidateSummary.getLocation().getLongitude().doubleValue()
        );
        com.javadocmd.simplelatlng.LatLng currentLatLng = new com.javadocmd.simplelatlng.LatLng(
                currentLocation.lat,
                currentLocation.lng
        );

        double distance = LatLngTool.distance(currentLatLng, candidateLatLng, LengthUnit.KILOMETER);
        BigDecimal roundedDistance = BigDecimal.valueOf(distance).setScale(3, RoundingMode.HALF_DOWN);

        return new DistanceVO(roundedDistance, DistanceVO.DistanceVOUnits.KILOMETER);
    }

}
