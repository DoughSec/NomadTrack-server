package com.nomadtrack.nomadtrackserver.repository;

import com.nomadtrack.nomadtrackserver.model.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Integer> {
    //check if user is already following the user
    boolean existsByFollower_IdAndFollowee_Id(Integer followerId, Integer followeeId);

    //check follow relationship to unfollow
    Optional<Follow> findByFollower_IdAndFollowee_Id(Integer followerId, Integer followeeId);

    //get all users that you follow
    List<Follow> findAllByFollower_Id(Integer followerId);

    //get all followers of a user
    List<Follow> findAllByFollowee_Id(Integer followeeId);

    //delete follow relationship
    void deleteByFollower_IdAndFollowee_Id(Integer followerId, Integer followeeId);

}
