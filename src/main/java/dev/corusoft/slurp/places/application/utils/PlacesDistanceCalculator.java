package dev.corusoft.slurp.places.application.utils;

import com.google.maps.model.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;
import dev.corusoft.slurp.places.domain.CandidateSummary;
import dev.corusoft.slurp.places.domain.location.DistanceVO;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PlacesDistanceCalculator {

    public static DistanceVO calculateDistance(CandidateSummary candidate, LatLng currentLocation) {
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
