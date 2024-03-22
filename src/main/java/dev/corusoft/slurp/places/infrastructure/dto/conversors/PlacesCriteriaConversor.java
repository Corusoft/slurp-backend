package dev.corusoft.slurp.places.infrastructure.dto.conversors;

import com.google.maps.model.PlaceType;
import com.google.maps.model.PriceLevel;
import dev.corusoft.slurp.places.application.criteria.PlacesCriteria;
import dev.corusoft.slurp.places.infrastructure.dto.PlacesCriteriaDTO;

public class PlacesCriteriaConversor {
    /* ******************** Convertir a Entidad ******************** */
    public static PlacesCriteria toPlacesCriteria(PlacesCriteriaDTO dto) {
        String cleanKeywords = (dto.getKeywords() != null) ? dto.getKeywords().strip() : null;

        PlacesCriteria.PlacesCriteriaBuilder entityBuilder = PlacesCriteria.builder()
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .radius(dto.getRadius())
                .placeType(parsePlaceType(dto.getPlaceType()))
                .keywords(cleanKeywords)
                .maxPriceLevel(parsePriceLevel(dto.getMaxPriceLevel()))
                .minPriceLevel(parsePriceLevel(dto.getMinPriceLevel()))
                .isOpenNow(dto.getIsOpenNow());

        return entityBuilder.build();
    }

    private static PlaceType parsePlaceType(String value) {
        PlaceType placeType;
        try {
            placeType = PlaceType.valueOf(value);
        } catch (IllegalArgumentException | NullPointerException ex) {
            placeType = null;
        }

        return placeType;
    }

    private static PriceLevel parsePriceLevel(String value) {
        PriceLevel priceLevel;
        try {
            priceLevel = PriceLevel.valueOf(value);
        } catch (IllegalArgumentException | NullPointerException ex) {
            priceLevel = null;
        }

        return priceLevel;
    }

}
