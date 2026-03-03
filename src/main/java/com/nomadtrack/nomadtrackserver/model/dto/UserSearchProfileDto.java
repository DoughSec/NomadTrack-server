package com.nomadtrack.nomadtrackserver.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSearchProfileDto {
    private Integer id;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private String bio;
    private Integer followerCount;
    private Integer followingCount;
}
