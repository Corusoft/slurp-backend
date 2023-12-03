package dev.corusoft.slurp.places.application;

import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Log4j2
@Transactional
@Service
public class PlacesServiceImpl implements PlacesService {
    /* DEPENDENCIES */
    private final GeoApiContext context;

    public PlacesServiceImpl(GeoApiContext context) {
        this.context = context;
    }

    /* USE CASES */

    @Override
    public void findNearbyPlaces(LatLng coordinates) {
        LatLng latLng = new LatLng(0.0, 0.0);
        PlacesSearchResponse response;
        try {
            response = PlacesApi.nearbySearchQuery(context, latLng)
                    .radius(10000)
                    .type(PlaceType.RESTAURANT)
                    .await();

        } catch (ApiException e) {
            log.error("ApiException: ", e);
        } catch (InterruptedException e) {
            log.error("InterruptedException: ", e);
        } catch (IOException e) {
            log.error("IOException: ", e);
        }

    }

    /* HELPER FUNCTIONS */


}
