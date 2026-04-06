package com.nomadtrack.nomadtrackserver.model.dto.recommendationDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponseDto {
    private List<DestinationRecommendationDto> recommendations;
}
