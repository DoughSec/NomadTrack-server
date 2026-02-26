package com.nomadtrack.nomadtrackserver.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TripLikeResponseDto {
    private Integer likeId;
    private Integer tripId;
    private Integer userId;
    private String userFirstName;
    private String userLastName;
    private LocalDateTime createdAt;
}

