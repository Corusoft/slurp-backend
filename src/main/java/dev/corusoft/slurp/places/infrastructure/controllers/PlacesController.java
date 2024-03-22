package dev.corusoft.slurp.places.infrastructure.controllers;

import dev.corusoft.slurp.common.api.ApiResponse;
import dev.corusoft.slurp.common.api.error.ServiceException;
import dev.corusoft.slurp.common.exception.InvalidArgumentException;
import dev.corusoft.slurp.common.exception.MissingMandatoryValueException;
import dev.corusoft.slurp.common.pagination.Block;
import dev.corusoft.slurp.common.pagination.BlockDTO;
import dev.corusoft.slurp.places.application.PlacesService;
import dev.corusoft.slurp.places.application.criteria.PlacesCriteria;
import dev.corusoft.slurp.places.domain.CandidateSummary;
import dev.corusoft.slurp.places.infrastructure.dto.CandidateSummaryDTO;
import dev.corusoft.slurp.places.infrastructure.dto.PlacesCriteriaDTO;
import dev.corusoft.slurp.places.infrastructure.dto.conversors.CandidateConversor;
import dev.corusoft.slurp.places.infrastructure.dto.conversors.PlacesCriteriaConversor;
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
    public ApiResponse<BlockDTO<CandidateSummaryDTO>> findCandidatesNearby(@RequestBody PlacesCriteriaDTO params) throws ServiceException, InvalidArgumentException, MissingMandatoryValueException {
        // Castear ubicación
        PlacesCriteria criteria = PlacesCriteriaConversor.toPlacesCriteria(params);

        // Petición al servicio
        Block<CandidateSummary> candidatesBlock = placesService.findCandidatesNearby(criteria);

        // Convertir a DTO
        List<CandidateSummaryDTO> items = CandidateConversor.toCandidateSummaryDTOList(candidatesBlock.getItems());
        BlockDTO<CandidateSummaryDTO> blockDto = new BlockDTO<>(items, candidatesBlock.getNextPageToken());

        return buildSuccessApiResponse(blockDto);
    }
}
