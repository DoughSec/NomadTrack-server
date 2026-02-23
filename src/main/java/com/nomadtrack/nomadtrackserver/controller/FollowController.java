package com.nomadtrack.nomadtrackserver.controller;

import com.nomadtrack.nomadtrackserver.model.Follow;
import com.nomadtrack.nomadtrackserver.model.User;
import com.nomadtrack.nomadtrackserver.repository.UserRepository;
import com.nomadtrack.nomadtrackserver.service.FollowService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/nomadTrack/follows")
public class FollowController {

    private final FollowService followService;
    private final UserRepository userRepository;

    public FollowController(FollowService followService,
                            UserRepository userRepository) {
        this.followService = followService;
        this.userRepository = userRepository;
    }

    // follow a user
    @PostMapping("/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Follow followUser(@PathVariable Integer userId,
                               Principal principal) {
        Integer currentUserId = getCurrentUserId(principal);

        return followService.follow(currentUserId, userId);
    }

    // unfollow a user
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unfollowUser(@PathVariable Integer userId,
                             Principal principal) {
        Integer currentUserId = getCurrentUserId(principal);

        followService.unfollow(currentUserId, userId);
    }

    // get current users a user follows
    @GetMapping("/following")
    public List<Follow> getFollowing(Principal principal) {
        Integer currentUserId = getCurrentUserId(principal);

        return followService.getFollowing(currentUserId);
    }

    // get current user's followers
    @GetMapping("/followers")
    public List<Follow> getFollowers(Principal principal) {
        Integer currentUserId = getCurrentUserId(principal);

        return followService.getFollowers(currentUserId);
    }

    private Integer getCurrentUserId(Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new IllegalArgumentException("User is not authenticated");
        }

        String email = principal.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));

        return currentUser.getId();
    }
}