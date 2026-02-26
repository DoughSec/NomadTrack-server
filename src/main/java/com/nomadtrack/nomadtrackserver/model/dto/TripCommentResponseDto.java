package com.nomadtrack.nomadtrackserver.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TripCommentResponseDto {
    private Integer commentId;
    private Integer tripId;
    private Integer userId;
    private String userFirstName;
    private String userLastName;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

