package com.nomadtrack.nomadtrackserver.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MapPinDto {

    private Integer tripId;
    private String city;
    private String country;
    private BigDecimal latitude;
    private BigDecimal longitude;

}