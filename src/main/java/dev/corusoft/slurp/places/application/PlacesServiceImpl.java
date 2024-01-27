package dev.corusoft.slurp.places.application;

import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;
import dev.corusoft.slurp.common.pagination.Block;
import dev.corusoft.slurp.common.visitors.google.GoogleMapsVisitor;
import dev.corusoft.slurp.common.visitors.google.GoogleMapsVisitorImpl;
import dev.corusoft.slurp.places.domain.Candidate;
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
    public Block<Candidate> findCandidatesNearby(SearchPerimeter perimeter) {
        List<Candidate> candidates = new ArrayList<>();

        PlacesSearchResponse apiRespose;
        Block<Candidate> result = new Block<>();
        try {
            // Hacer petición a Maps
            LatLng latLng = new LatLng(perimeter.getYCoordinate(), perimeter.getXCoordinate());
            apiRespose = PlacesApi.nearbySearchQuery(context, latLng)
                    .type(PlaceType.RESTAURANT, PlaceType.BAR)
                    .radius((int) perimeter.getRadius())
                    .await();

            // Generar candidatos con los resultados de Maps
            candidates = Arrays.stream(apiRespose.results)
                    .map(gMapsVisitor::visit)
                    .map(candidate -> {
                        candidate.setDistance(calculateDistance(candidate, latLng));
                        return candidate;
                    })
                    .toList();
            result.setItems(candidates);
            result.setHasMoreItems(apiRespose.nextPageToken != null);
        } catch (ApiException e) {
            log.error("ApiException: ", e);
        } catch (InterruptedException e) {
            log.error("InterruptedException: ", e);
        } catch (IOException e) {
            log.error("IOException: ", e);
        }

        return result;
    }


    /* HELPER FUNCTIONS */
    private DistanceVO calculateDistance(Candidate candidate, LatLng currentLocation) {

        com.javadocmd.simplelatlng.LatLng candidateLatLng = new com.javadocmd.simplelatlng.LatLng(
                candidate.getLocation().getLatitude().doubleValue(),
                candidate.getLocation().getLongitude().doubleValue()
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
