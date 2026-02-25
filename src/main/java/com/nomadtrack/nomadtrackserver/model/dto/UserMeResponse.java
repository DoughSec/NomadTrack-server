package com.nomadtrack.nomadtrackserver.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class UserMeResponse {

    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    private String bio;
    private String address;
    private String avatarUrl;

}