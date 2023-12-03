package dev.corusoft.slurp.places.infrastructure;

import com.google.maps.model.LatLng;
import dev.corusoft.slurp.places.application.PlacesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/places")
public class PlacesController {
    private final PlacesService placesService;

    public PlacesController(PlacesService placesService) {
        this.placesService = placesService;
    }

    @GetMapping(path = "/findNear")
    public void findNear() {
        placesService.findNearbyPlaces(new LatLng());
    }
}
