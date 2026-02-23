package com.nomadtrack.nomadtrackserver.repository;

import com.nomadtrack.nomadtrackserver.model.Follower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowerRepository extends JpaRepository<Follower, Integer> {
    //check if user is already following the user
    boolean existsByFollower_IdAndFollowee_Id(Integer followerId, Integer followeeId);

    //check follow relationship to unfollow
    Optional<Follower> findByFollower_IdAndFollowee_Id(Integer followerId, Integer followeeId);

    //get all users that you follow
    List<Follower> findAllByFollower_Id(Integer followerId);

    //get all followers of a user
    List<Follower> findAllByFollowee_Id(Integer followeeId);

    //delete follow relationship
    void deleteByFollower_IdAndFollowee_Id(Integer followerId, Integer followeeId);

}
