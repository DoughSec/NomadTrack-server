package com.nomadtrack.nomadtrackserver.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FollowDto {

    private Integer id;
    private Integer followerId;
    private String followerFirstName;
    private String followerLastName;
    private String followerAvatarUrl;
    private Integer followeeId;
    private String followeeFirstName;
    private String followeeLastName;
    private String followeeAvatarUrl;
    private LocalDateTime createdAt;
}

