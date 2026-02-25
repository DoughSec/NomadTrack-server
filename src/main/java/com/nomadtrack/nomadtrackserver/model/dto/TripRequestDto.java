package com.nomadtrack.nomadtrackserver.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class TripRequestDto {
    private String title;
    private String city;
    private String country;
    private LocalDate startDate;
    private LocalDate endDate;
    private String notes;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String visibility;
}
