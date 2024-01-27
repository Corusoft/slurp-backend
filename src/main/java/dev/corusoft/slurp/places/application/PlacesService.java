package dev.corusoft.slurp.places.application;

import dev.corusoft.slurp.common.pagination.Block;
import dev.corusoft.slurp.places.domain.Candidate;
import dev.corusoft.slurp.places.domain.SearchPerimeter;

public interface PlacesService {
    /**
     * Genera una lista de candidatos a partir de una ubicación y un radio de búsqueda
     *
     * @param perimeter Ubicación y radio de búsqueda
     * @return Lista de candidatos encontrados
     */
    Block<Candidate> findCandidatesNearby(SearchPerimeter perimeter);

}
