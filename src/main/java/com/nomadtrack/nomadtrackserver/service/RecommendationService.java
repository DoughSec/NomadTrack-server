package com.nomadtrack.nomadtrackserver.service;

import com.nomadtrack.nomadtrackserver.configAI.AiServiceProperties;
import com.nomadtrack.nomadtrackserver.model.dto.recommendationDTO.AiRecommendationRequestDto;
import com.nomadtrack.nomadtrackserver.model.dto.recommendationDTO.AiRecommendationResponseDto;
import com.nomadtrack.nomadtrackserver.model.dto.recommendationDTO.RecommendationRequestDto;
import com.nomadtrack.nomadtrackserver.model.dto.recommendationDTO.RecommendationResponseDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RecommendationService {

    private final RestTemplate restTemplate;
    private final AiServiceProperties aiServiceProperties;

    public RecommendationService(RestTemplate restTemplate, AiServiceProperties aiServiceProperties) {
        this.restTemplate = restTemplate;
        this.aiServiceProperties = aiServiceProperties;
    }

    public RecommendationResponseDto getRecommendations(RecommendationRequestDto requestDto) {
        String url = aiServiceProperties.getBaseUrl() + aiServiceProperties.getRecommendPath();
        AiRecommendationRequestDto aiRequest = mapToAiRequest(requestDto);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AiRecommendationRequestDto> entity = new HttpEntity<>(aiRequest, headers);

        ResponseEntity<AiRecommendationResponseDto> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                AiRecommendationResponseDto.class
        );

        AiRecommendationResponseDto responseBody = response.getBody();

        if (responseBody == null || responseBody.getRecommendations() == null) {
            throw new RuntimeException("AI service returned an empty response.");
        }

        return new RecommendationResponseDto(responseBody.getRecommendations());
    }

    private AiRecommendationRequestDto mapToAiRequest(RecommendationRequestDto requestDto) {
        AiRecommendationRequestDto aiRequest = new AiRecommendationRequestDto();
        aiRequest.setBudget(requestDto.getBudget());
        aiRequest.setClimate(requestDto.getClimate());
        aiRequest.setTripStyle(requestDto.getTripStyle());
        aiRequest.setActivities(requestDto.getActivities());
        aiRequest.setRegion(requestDto.getRegion());
        aiRequest.setTripType(requestDto.getTripType());
        aiRequest.setTripLength(requestDto.getTripLength());
        aiRequest.setTopN(requestDto.getTopN());
        return aiRequest;
    }
}