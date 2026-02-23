package com.nomadtrack.nomadtrackserver.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TripPhotoDto {
    private String url;
    private String caption;
    private Integer sortOrder;
}
