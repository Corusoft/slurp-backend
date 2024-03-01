package dev.corusoft.slurp.places.application;

import com.google.maps.model.PlacesSearchResponse;
import dev.corusoft.slurp.places.application.criteria.PlacesCriteria;

public interface GoogleApiService {
    PlacesSearchResponse findNearbyPlaces(PlacesCriteria criteria);
}
