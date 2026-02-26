package com.nomadtrack.nomadtrackserver.service;

import com.nomadtrack.nomadtrackserver.model.Follow;
import com.nomadtrack.nomadtrackserver.model.User;
import com.nomadtrack.nomadtrackserver.model.dto.FollowDto;
import com.nomadtrack.nomadtrackserver.repository.FollowRepository;
import com.nomadtrack.nomadtrackserver.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public FollowService(FollowRepository followRepository,
                         UserRepository userRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    //follow a user
    @Transactional
    public FollowDto follow(Integer followerId, Integer followeeId) {

        if (followerId == null || followeeId == null) {
            throw new IllegalArgumentException("FollowerId and FolloweeId must not be null");
        }

        if (followerId.equals(followeeId)) {
            throw new IllegalArgumentException("A user cannot follow themselves");
        }

        if (followRepository.existsByFollower_IdAndFollowee_Id(followerId, followeeId)) {
            throw new IllegalArgumentException("User is already following this user");
        }

        User followerUser = userRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("Follower not found"));

        User followeeUser = userRepository.findById(followeeId)
                .orElseThrow(() -> new IllegalArgumentException("Followee not found"));

        Follow relationship = new Follow();
        relationship.setFollower(followerUser);
        relationship.setFollowee(followeeUser);

        return toDto(followRepository.save(relationship));
    }

    //Unfollow a user
    @Transactional
    public void unfollow(Integer followerId, Integer followeeId) {

        Follow relationship = followRepository
                .findByFollower_IdAndFollowee_Id(followerId, followeeId)
                .orElseThrow(() -> new IllegalArgumentException("Follow relationship does not exist"));

        followRepository.delete(relationship);
    }

    //check for following or not
    public boolean isFollowing(Integer followerId, Integer followeeId) {
        if (followerId == null || followeeId == null) {
            return false;
        }

        return followRepository
                .existsByFollower_IdAndFollowee_Id(followerId, followeeId);
    }

    //Get all users that this user is following
    @Transactional
    public List<FollowDto> getFollowing(Integer followerId) {
        return followRepository.findAllByFollower_Id(followerId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    // Get all followers of a user
    @Transactional
    public List<FollowDto> getFollowers(Integer followeeId) {
        return followRepository.findAllByFollowee_Id(followeeId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    // Count followers
    public Integer countFollowers(Integer followeeId) {
        return followRepository.findAllByFollowee_Id(followeeId).size();
    }

    // Count who the user is following
    public Integer countFollowing(Integer followerId) {
        return followRepository.findAllByFollower_Id(followerId).size();
    }

    private FollowDto toDto(Follow follow) {
        User follower = follow.getFollower();
        User followee = follow.getFollowee();
        return new FollowDto(
                follow.getId(),
                follower.getId(),
                follower.getFirstName(),
                follower.getLastName(),
                follower.getAvatarURL(),
                followee.getId(),
                followee.getFirstName(),
                followee.getLastName(),
                followee.getAvatarURL(),
                follow.getCreatedAt()
        );
    }
}