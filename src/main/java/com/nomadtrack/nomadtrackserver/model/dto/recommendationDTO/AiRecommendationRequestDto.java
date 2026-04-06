package com.nomadtrack.nomadtrackserver.model.dto.recommendationDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AiRecommendationRequestDto {

    private String budget;
    private String climate;

    @JsonProperty("trip_style")
    private String tripStyle;

    private List<String> activities;
    private String region;

    @JsonProperty("trip_type")
    private String tripType;

    @JsonProperty("trip_length")
    private Integer tripLength;

    @JsonProperty("top_n")
    private Integer topN;

}
