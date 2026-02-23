package com.nomadtrack.nomadtrackserver.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileDto {
    private String firstName;
    private String lastName;
    private String avatarURL;
    private String email;
    private String passwordHash;
    private String bio;
    private String address;
}
