package com.nomadtrack.nomadtrackserver.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {

    private Integer userId;
    private String comment;

}
