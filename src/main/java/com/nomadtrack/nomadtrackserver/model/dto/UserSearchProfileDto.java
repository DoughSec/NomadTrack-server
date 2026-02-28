package com.nomadtrack.nomadtrackserver.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSearchProfileDto {
    private Integer id;
    private String firstName;
    private String lastName;
    private String avatarURL;
    private String bio;
}
