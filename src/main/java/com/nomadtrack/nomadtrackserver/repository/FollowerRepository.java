package com.nomadtrack.nomadtrackserver.repository;

import com.nomadtrack.nomadtrackserver.model.Follower;
import com.nomadtrack.nomadtrackserver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowerRepository extends JpaRepository<Follower, Integer> {
    //check if user is already following the user
    boolean existsByFollowerAndFollowee(User follower, User followee);

    //check follow relationship to unfollow
    Optional<Follower> findByFollowerAndFollowee(User follower, User followee);

    //get all users that you follow
    List<Follower> findAllByFollower(User follower);

    //get all followers of a user
    List<Follower> findAllByFollowee(User followee);

    //delete follow relationship
    void deleteByFollowerAndFollowee(User follower, User followee);

}
