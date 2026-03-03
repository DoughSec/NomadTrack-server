package com.nomadtrack.nomadtrackserver.controller;

import com.nomadtrack.nomadtrackserver.model.dto.FollowDto;
import com.nomadtrack.nomadtrackserver.model.dto.UserMeResponse;
import com.nomadtrack.nomadtrackserver.service.AuthenticationService;
import com.nomadtrack.nomadtrackserver.service.FollowService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nomadTrack/follows")
public class FollowController {

    private final FollowService followService;
    private final AuthenticationService authService;

    public FollowController(FollowService followService, AuthenticationService authService) {
        this.followService = followService;
        this.authService = authService;
    }

    // follow a user
    @PostMapping("/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public FollowDto followUser(@PathVariable Integer userId,
                                @RequestHeader("Authorization") String authorizationHeader) {

        String token = authService.extractBearerToken(authorizationHeader);
        UserMeResponse userMeResponse = authService.me(token);
        Integer currentUserId = userMeResponse.getId();
        return followService.follow(currentUserId, userId);
    }

    // unfollow a user
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unfollowUser(@PathVariable Integer userId,
                             @RequestHeader("Authorization") String authorizationHeader) {
        String token = authService.extractBearerToken(authorizationHeader);
        UserMeResponse userMeResponse = authService.me(token);
        Integer currentUserId = userMeResponse.getId();

        followService.unfollow(currentUserId, userId);
    }

    // get current user's following list
    @GetMapping("/following")
    public List<FollowDto> getFollowing(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authService.extractBearerToken(authorizationHeader);
        UserMeResponse userMeResponse = authService.me(token);
        return followService.getFollowing(userMeResponse.getId());
    }

    // get current user's followers list
    @GetMapping("/followers")
    public List<FollowDto> getFollowers(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authService.extractBearerToken(authorizationHeader);
        UserMeResponse userMeResponse = authService.me(token);
        return followService.getFollowers(userMeResponse.getId());
    }

    // get any user's following list by userId
    @GetMapping("/{userId}/following")
    public List<FollowDto> getFollowingByUserId(@PathVariable Integer userId) {
        return followService.getFollowing(userId);
    }

    // get any user's followers list by userId
    @GetMapping("/{userId}/followers")
    public List<FollowDto> getFollowersByUserId(@PathVariable Integer userId) {
        return followService.getFollowers(userId);
    }
}