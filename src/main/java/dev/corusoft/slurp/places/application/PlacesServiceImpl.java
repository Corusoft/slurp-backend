package dev.corusoft.slurp.places.application;

import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;
import dev.corusoft.slurp.common.pagination.Block;
import dev.corusoft.slurp.google.visitors.GoogleMapsVisitor;
import dev.corusoft.slurp.google.visitors.GoogleMapsVisitorImpl;
import dev.corusoft.slurp.places.domain.CandidateSummary;
import dev.corusoft.slurp.places.domain.SearchPerimeter;
import dev.corusoft.slurp.places.domain.location.DistanceVO;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

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

    @Override
    public Block<CandidateSummary> findCandidatesNearby(SearchPerimeter perimeter) {
        List<CandidateSummary> candidateSummaries = new ArrayList<>();

        PlacesSearchResponse apiResponse;
        boolean hasMoreItems = false;
        try {
            // Hacer petición a Maps
            LatLng latLng = new LatLng(perimeter.getYCoordinate(), perimeter.getXCoordinate());
            apiResponse = PlacesApi.nearbySearchQuery(context, latLng)
                    .type(PlaceType.RESTAURANT, PlaceType.BAR)
                    .radius((int) perimeter.getRadius())
                    .await();

            // Generar candidatos con los resultados de Maps
            candidateSummaries = Arrays.stream(apiResponse.results)
                    .map(gMapsVisitor::visit)
                    .map(candidate -> {
                        candidate.setDistance(calculateDistance(candidate, latLng));
                        return candidate;
                    })
                    .toList();

            hasMoreItems = apiResponse.nextPageToken != null;
        } catch (ApiException e) {
            log.error("ApiException: ", e);
        } catch (InterruptedException e) {
            log.error("InterruptedException: ", e);
        } catch (IOException e) {
            log.error("IOException: ", e);
        }

        Block<CandidateSummary> block = new Block<>(candidateSummaries, hasMoreItems);

        return block;
    }


    /* HELPER FUNCTIONS */
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
