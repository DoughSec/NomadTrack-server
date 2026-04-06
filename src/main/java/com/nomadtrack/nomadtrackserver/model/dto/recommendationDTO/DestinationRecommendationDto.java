package com.nomadtrack.nomadtrackserver.model.dto.recommendationDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DestinationRecommendationDto {
    private String destination;
    private String country;
    private String region;

    @JsonProperty("trip_style")
    private String tripStyle;

    @JsonProperty("trip_type")
    private String tripType;

    private String activities;
    private Double score;

}
