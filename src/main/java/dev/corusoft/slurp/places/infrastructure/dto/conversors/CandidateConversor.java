package dev.corusoft.slurp.places.infrastructure.dto.conversors;

import dev.corusoft.slurp.places.domain.CandidateSummary;
import dev.corusoft.slurp.places.infrastructure.dto.CandidateSummaryDTO;

import java.util.List;

public class CandidateConversor {
    /* ******************** Convertir a DTO ******************** */
    public static CandidateSummaryDTO toCandidateSummaryDTO(CandidateSummary entity) {
        return CandidateSummaryDTO.builder()
                .placeId(entity.getPlaceId())
                .name(entity.getName())
                .location(entity.getLocation())
                .formattedAddress(entity.getFormattedAddress())
                .types(entity.getTypes())
                .distance(entity.getDistance())
                .build();
    }


    /* ******************** Convertir a conjunto de DTO ******************** */
    public static List<CandidateSummaryDTO> toCandidateSummaryDTOList(List<CandidateSummary> entityList) {
        return entityList.stream().map(CandidateConversor::toCandidateSummaryDTO).toList();
    }

}
