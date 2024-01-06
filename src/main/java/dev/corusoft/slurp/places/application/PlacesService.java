package dev.corusoft.slurp.places.application;

import dev.corusoft.slurp.places.domain.Candidate;
import dev.corusoft.slurp.places.domain.SearchPerimeter;

import java.util.List;

public interface PlacesService {
    /**
     * Genera una lista de candidatos a partir de una ubicación y un radio de búsqueda
     *
     * @param perimeter Ubicación y radio de búsqueda
     * @return Lista de candidatos encontrados
     */
    List<Candidate> findCandidatesNearby(SearchPerimeter perimeter);

}
