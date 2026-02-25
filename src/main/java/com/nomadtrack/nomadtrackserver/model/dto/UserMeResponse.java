package com.nomadtrack.nomadtrackserver.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserMeResponse {

    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    private String bio;
    private String address;
    private String avatarUrl;

}