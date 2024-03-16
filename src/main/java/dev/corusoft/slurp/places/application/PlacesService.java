package dev.corusoft.slurp.places.application;

import dev.corusoft.slurp.common.api.error.ServiceException;
import dev.corusoft.slurp.common.pagination.Block;
import dev.corusoft.slurp.places.application.criteria.PlacesCriteria;
import dev.corusoft.slurp.places.domain.CandidateSummary;
import jakarta.validation.ConstraintViolationException;

public interface PlacesService {
    /**
     * Genera una lista de candidatos a partir de una ubicación y un radio de búsqueda
     *
     * @param criteria Criterios de búsqueda
     * @throws ServiceException - Error procesando petición
     * @return Lista de candidatos encontrados
     */
    Block<CandidateSummary> findCandidatesNearby(PlacesCriteria criteria) throws ServiceException, ConstraintViolationException;

}
