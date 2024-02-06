package dev.corusoft.slurp.places.infrastructure.controllers;

import dev.corusoft.slurp.common.api.ApiResponse;
import dev.corusoft.slurp.common.pagination.Block;
import dev.corusoft.slurp.common.pagination.BlockDTO;
import dev.corusoft.slurp.places.application.PlacesService;
import dev.corusoft.slurp.places.domain.CandidateSummary;
import dev.corusoft.slurp.places.domain.SearchPerimeter;
import dev.corusoft.slurp.places.infrastructure.dto.CandidateSummaryDTO;
import dev.corusoft.slurp.places.infrastructure.dto.conversors.CandidateConversor;
import dev.corusoft.slurp.places.infrastructure.dto.conversors.SearchParamsConversor;
import dev.corusoft.slurp.places.infrastructure.dto.input.SearchPerimeterParamsDTO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static dev.corusoft.slurp.common.api.ApiResponseHelper.buildSuccessApiResponse;


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
    public ApiResponse<BlockDTO<CandidateSummaryDTO>> findCandidatesNearby(@RequestBody SearchPerimeterParamsDTO params) {
        // Castear ubicación
        SearchPerimeter searchPerimeter = SearchParamsConversor.fromSearchPerimeterParamsDTO(params);

        // Petición al servicio
        Block<CandidateSummary> candidatesBlock = placesService.findCandidatesNearby(searchPerimeter);

        // Convertir a DTO
        List<CandidateSummaryDTO> items = CandidateConversor.toCandidateSummaryDTOList(candidatesBlock.getItems());
        BlockDTO<CandidateSummaryDTO> blockDto = new BlockDTO<>(items, candidatesBlock.hasMoreItems());

        return buildSuccessApiResponse(blockDto);
    }
}
