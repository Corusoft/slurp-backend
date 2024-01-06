package dev.corusoft.slurp.places.infrastructure.controllers;

import dev.corusoft.slurp.places.application.PlacesService;
import dev.corusoft.slurp.places.domain.Candidate;
import dev.corusoft.slurp.places.domain.SearchPerimeter;
import dev.corusoft.slurp.places.infrastructure.dto.CandidateSummaryDTO;
import dev.corusoft.slurp.places.infrastructure.dto.conversors.CandidateConversor;
import dev.corusoft.slurp.places.infrastructure.dto.conversors.SearchParamsConversor;
import dev.corusoft.slurp.places.infrastructure.dto.input.SearchPerimeterParamsDTO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/places")
public class PlacesController {
    private final PlacesService placesService;

    public PlacesController(PlacesService placesService) {
        this.placesService = placesService;
    }


    @PostMapping(path = "/findNearby",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<CandidateSummaryDTO> findCandidatesNearby(@RequestBody SearchPerimeterParamsDTO params) {
        // Castear ubicación
        SearchPerimeter searchPerimeter = SearchParamsConversor.fromSearchPerimeterParamsDTO(params);

        // Petición al servicio
        List<Candidate> candidates = placesService.findCandidatesNearby(searchPerimeter);

        // Convertir a DTO
        return CandidateConversor.toCandidateSummaryDTOList(candidates);
    }
}
