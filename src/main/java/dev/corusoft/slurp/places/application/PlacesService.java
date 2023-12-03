package dev.corusoft.slurp.places.application;

import com.google.maps.model.LatLng;

public interface PlacesService {
    void findNearbyPlaces(LatLng coordinates);
}
