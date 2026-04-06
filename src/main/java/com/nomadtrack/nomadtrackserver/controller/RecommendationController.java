package com.nomadtrack.nomadtrackserver.controller;

import com.nomadtrack.nomadtrackserver.model.dto.recommendationDTO.RecommendationRequestDto;
import com.nomadtrack.nomadtrackserver.model.dto.recommendationDTO.RecommendationResponseDto;
import com.nomadtrack.nomadtrackserver.service.RecommendationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/nomadTrack/recommendations")
@CrossOrigin
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping
    public ResponseEntity<RecommendationResponseDto> getRecommendations(
            @Valid @RequestBody RecommendationRequestDto requestDto
    ) {
        RecommendationResponseDto response = recommendationService.getRecommendations(requestDto);
        return ResponseEntity.ok(response);
    }
}
