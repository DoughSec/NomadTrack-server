package com.nomadtrack.nomadtrackserver.model.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class TripResponseDto {
    private Integer id;
    private Integer userId;
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
