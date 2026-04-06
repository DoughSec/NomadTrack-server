package com.nomadtrack.nomadtrackserver.model.dto.recommendationDTO;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RecommendationRequestDto {
    @NotBlank
    private String budget;

    @NotBlank
    private String climate;

    @NotBlank
    private String tripStyle;

    @NotEmpty
    private List<String> activities;

    @NotBlank
    private String region;

    @NotBlank
    private String tripType;

    @NotNull
    @Min(1)
    @Max(30)
    private Integer tripLength;

    @NotNull
    @Min(1)
    @Max(10)
    private Integer topN;
}
