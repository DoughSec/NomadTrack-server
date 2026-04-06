package com.nomadtrack.nomadtrackserver.model.dto.recommendationDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AiRecommendationResponseDto {
    private List<DestinationRecommendationDto> recommendations;
}