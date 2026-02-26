package com.nomadtrack.nomadtrackserver.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class WishlistResponseDto {
    private Integer wishlistId;
    private String title;
    private String description;
    private String targetCountry;
    private String targetCity;
    private LocalDate deadline;
    private boolean completed;
    private LocalDate completedDate;
}
