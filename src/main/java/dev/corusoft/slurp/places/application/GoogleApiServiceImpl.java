package dev.corusoft.slurp.places.application;

import com.google.maps.*;
import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResponse;
import dev.corusoft.slurp.common.api.error.ServiceException;
import dev.corusoft.slurp.places.application.criteria.PlacesCriteria;
import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Log4j2
@Service
@NoArgsConstructor
public class GoogleApiServiceImpl implements GoogleApiService {
    /* DEPENDENCIES */
    private GeoApiContext geoApiContext;

    public GoogleApiServiceImpl(GeoApiContext geoApiContext) {
        this.geoApiContext = geoApiContext;
    }


    /* USE CASES */

    @Override
    public PlacesSearchResponse findNearbyPlaces(@Valid PlacesCriteria criteria) throws ServiceException {
        // Configurar petición
        LatLng position = new LatLng(criteria.getLatitude(), criteria.getLongitude());
        NearbySearchRequest request = createNearbySearchRequest(position);
        configureRequestUsingCriteria(request, criteria);

        // Ejecutar petición
        PlacesSearchResponse apiResponse = executeRequest(request);

        return apiResponse;
    }

    /* HELPER METHODS */
    private NearbySearchRequest createNearbySearchRequest(LatLng position) {
        return PlacesApi.nearbySearchQuery(geoApiContext, position);
    }

    private void configureRequestUsingCriteria(NearbySearchRequest request, PlacesCriteria criteria) {
        if (criteria.getRadius() != null) request.radius(criteria.getRadius());
        if (criteria.getPlaceType() != null) request.type(criteria.getPlaceType());
        if (criteria.getKeywords() != null) request.keyword(criteria.getKeywords());
        if (criteria.getMinPriceLevel() != null) request.minPrice(criteria.getMinPriceLevel());
        if (criteria.getMaxPriceLevel() != null) request.maxPrice(criteria.getMaxPriceLevel());
        if (criteria.getIsOpenNow() != null) request.openNow(criteria.getIsOpenNow());
    }

    PlacesSearchResponse executeRequest(NearbySearchRequest request) throws ServiceException {
        try {
            return request.await();
        } catch (ApiException | InterruptedException | IOException e) {
            log.error("An {} ocurred while executing request: \n{}", e.getClass().getSimpleName(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

}
