package com.nomadtrack.nomadtrackserver.test;

import com.nomadtrack.nomadtrackserver.exception.BadRequestException;
import com.nomadtrack.nomadtrackserver.exception.ResourceNotFoundException;
import com.nomadtrack.nomadtrackserver.model.Follow;
import com.nomadtrack.nomadtrackserver.model.User;
import com.nomadtrack.nomadtrackserver.model.dto.FollowDto;
import com.nomadtrack.nomadtrackserver.repository.FollowRepository;
import com.nomadtrack.nomadtrackserver.repository.UserRepository;
import com.nomadtrack.nomadtrackserver.service.FollowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FollowServiceTest {

    @Mock
    private FollowRepository followRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FollowService followService;

    private User follower;
    private User followee;

    @BeforeEach
    void setUp() {
        follower = new User();
        follower.setId(1);
        followee = new User();
        followee.setId(2);
    }

    // --- follow() tests ---

    @Test
    void follow_success() {
        when(followRepository.existsByFollower_IdAndFollowee_Id(1, 2)).thenReturn(false);
        when(userRepository.findById(1)).thenReturn(Optional.of(follower));
        when(userRepository.findById(2)).thenReturn(Optional.of(followee));
        when(followRepository.save(any(Follow.class))).thenAnswer(i -> {
            Follow f = i.getArgument(0);
            f.setId(1);
            return f;
        });

        FollowDto result = followService.follow(1, 2);

        assertNotNull(result);
        assertEquals(1, result.getFollowerId());
        assertEquals(2, result.getFolloweeId());
        verify(followRepository).save(any(Follow.class));
    }

    @Test
    void follow_nullFollowerId_throws() {
        assertThrows(BadRequestException.class, () -> followService.follow(null, 2));
    }

    @Test
    void follow_nullFolloweeId_throws() {
        assertThrows(BadRequestException.class, () -> followService.follow(1, null));
    }

    @Test
    void follow_selfFollow_throws() {
        assertThrows(BadRequestException.class, () -> followService.follow(1, 1));
    }

    @Test
    void follow_alreadyFollowing_throws() {
        when(followRepository.existsByFollower_IdAndFollowee_Id(1, 2)).thenReturn(true);
        assertThrows(BadRequestException.class, () -> followService.follow(1, 2));
    }

    @Test
    void follow_followerNotFound_throws() {
        when(followRepository.existsByFollower_IdAndFollowee_Id(1, 2)).thenReturn(false);
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> followService.follow(1, 2));
    }

    @Test
    void follow_followeeNotFound_throws() {
        when(followRepository.existsByFollower_IdAndFollowee_Id(1, 2)).thenReturn(false);
        when(userRepository.findById(1)).thenReturn(Optional.of(follower));
        when(userRepository.findById(2)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> followService.follow(1, 2));
    }

    // --- unfollow() tests ---

    @Test
    void unfollow_success() {
        Follow follow = new Follow();
        when(followRepository.findByFollower_IdAndFollowee_Id(1, 2)).thenReturn(Optional.of(follow));

        followService.unfollow(1, 2);

        verify(followRepository).delete(follow);
    }

    @Test
    void unfollow_notFound_throws() {
        when(followRepository.findByFollower_IdAndFollowee_Id(1, 2)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> followService.unfollow(1, 2));
    }

    // --- isFollowing() tests ---

    @Test
    void isFollowing_returnsTrue() {
        when(followRepository.existsByFollower_IdAndFollowee_Id(1, 2)).thenReturn(true);
        assertTrue(followService.isFollowing(1, 2));
    }

    @Test
    void isFollowing_returnsFalse() {
        when(followRepository.existsByFollower_IdAndFollowee_Id(1, 2)).thenReturn(false);
        assertFalse(followService.isFollowing(1, 2));
    }

    @Test
    void isFollowing_nullFollowerId_returnsFalse() {
        assertFalse(followService.isFollowing(null, 2));
    }

    @Test
    void isFollowing_nullFolloweeId_returnsFalse() {
        assertFalse(followService.isFollowing(1, null));
    }

    // --- getFollowing() / getFollowers() tests ---

    @Test
    void getFollowing_returnsList() {
        Follow f = new Follow();
        f.setFollower(follower);
        f.setFollowee(followee);
        when(followRepository.findAllByFollower_Id(1)).thenReturn(List.of(f));

        List<FollowDto> result = followService.getFollowing(1);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getFollowerId());
    }

    @Test
    void getFollowers_returnsList() {
        Follow f = new Follow();
        f.setFollower(follower);
        f.setFollowee(followee);
        when(followRepository.findAllByFollowee_Id(2)).thenReturn(List.of(f));

        List<FollowDto> result = followService.getFollowers(2);

        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getFolloweeId());
    }

    // --- countFollowers() / countFollowing() tests ---

    @Test
    void countFollowers_returnsCorrectCount() {
        when(followRepository.findAllByFollowee_Id(2)).thenReturn(List.of(new Follow(), new Follow()));
        assertEquals(2, followService.countFollowers(2));
    }

    @Test
    void countFollowing_returnsCorrectCount() {
        when(followRepository.findAllByFollower_Id(1)).thenReturn(List.of(new Follow()));
        assertEquals(1, followService.countFollowing(1));
    }

    @Test
    void countFollowers_noFollowers_returnsZero() {
        when(followRepository.findAllByFollowee_Id(2)).thenReturn(List.of());
        assertEquals(0, followService.countFollowers(2));
    }

    @Test
    void countFollowing_noFollowing_returnsZero() {
        when(followRepository.findAllByFollower_Id(1)).thenReturn(List.of());
        assertEquals(0, followService.countFollowing(1));
    }
}
