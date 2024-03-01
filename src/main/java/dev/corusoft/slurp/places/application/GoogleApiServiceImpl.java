package dev.corusoft.slurp.places.application;

import com.google.maps.*;
import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResponse;
import dev.corusoft.slurp.places.application.criteria.PlacesCriteria;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Log4j2
@Service
public class GoogleApiServiceImpl implements GoogleApiService {
    /* DEPENDENCIES */
    private final GeoApiContext geoApiContext;

    public GoogleApiServiceImpl(GeoApiContext geoApiContext) {
        this.geoApiContext = geoApiContext;
    }


    /* USE CASES */

    @Override
    public PlacesSearchResponse findNearbyPlaces(PlacesCriteria criteria) {
        // Configurar petición
        LatLng position = new LatLng(criteria.getLatitude(), criteria.getLongitude());
        NearbySearchRequest request = PlacesApi.nearbySearchQuery(geoApiContext, position);
        configureRequestUsingCriteria(request, criteria);

        // Ejecutar petición
        PlacesSearchResponse apiResponse = executeRequest(request);

        return apiResponse;
    }

    /* HELPER METHODS */
    private void configureRequestUsingCriteria(NearbySearchRequest request, PlacesCriteria criteria) {
        if (criteria.getRadius() != null) request.radius(criteria.getRadius());
        if (criteria.getPlaceType() != null) request.type(criteria.getPlaceType());
        if (criteria.getKeywords() != null) request.keyword(criteria.getKeywords());
        if (criteria.getMinPriceLevel() != null) request.minPrice(criteria.getMinPriceLevel());
        if (criteria.getMaxPriceLevel() != null) request.maxPrice(criteria.getMaxPriceLevel());
        if (criteria.getIsOpenNow() != null) request.openNow(criteria.getIsOpenNow());
    }

    private PlacesSearchResponse executeRequest(NearbySearchRequest request) {
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

}
