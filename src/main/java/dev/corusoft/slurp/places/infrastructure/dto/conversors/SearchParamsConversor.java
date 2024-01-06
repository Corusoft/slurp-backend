package dev.corusoft.slurp.places.infrastructure.dto.conversors;

import dev.corusoft.slurp.places.domain.SearchPerimeter;
import dev.corusoft.slurp.places.infrastructure.dto.input.SearchPerimeterParamsDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchParamsConversor {
    /* ******************** Convertir a DTO ******************** */

    /* ******************** Convertir a entidad ******************** */
    public static SearchPerimeter fromSearchPerimeterParamsDTO(SearchPerimeterParamsDTO dto) {
        return new SearchPerimeter(
                dto.getLongitude(),
                dto.getLatitude(),
                dto.getRadius()
        );
    }
}
