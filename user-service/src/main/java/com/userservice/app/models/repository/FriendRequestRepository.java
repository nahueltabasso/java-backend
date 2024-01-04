package com.userservice.app.models.repository;

import com.userservice.app.models.entity.FriendRequest;
import nrt.common.microservice.models.repository.CommonEntityRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRequestRepository extends CommonEntityRepository<FriendRequest> {

    @Query(value = "select fr.* " +
            "from user_friend_requests as fr " +
            "where fr.from_user_id = ?1 and fr.to_user_id = ?2 and fr.status = true " +
            "or fr.from_user_id = ?1 and fr.to_user_id = ?2 and fr.status = true;", nativeQuery = true)
    public List<FriendRequest> findPreviousFriendsRequestsWithStatusTrue(Long fromUserId, Long toUserId);

    @Query(value = "select fr.* " +
            "from user_friend_requests as fr " +
            "where fr.from_user_id = ?1 and fr.to_user_id = ?2 and fr.status = false " +
            "or fr.from_user_id = ?1 and fr.to_user_id = ?2 and fr.status = false;", nativeQuery = true)
    public List<FriendRequest> findPreviousFriendsRequestsWithStatusFalse(Long fromUserId, Long toUserId);

    @Query(value = "select fr.* " +
            "from user_friend_requests as fr " +
            "where fr.to_user_id  = (select id from user_users_profiles where user_id = ?1);", nativeQuery = true)
    public List<FriendRequest> findPendingFriendsRequestsByUserId(Long userId);

    @Query(value = "SELECT user_id AS user_id " +
            "FROM ( " +
            "    SELECT from_user_id AS user_id " +
            "    FROM user_friend_requests " +
            "    WHERE status = true " +
            "    and (from_user_id = (select id from user_users_profiles where user_id = ?1)) " +
            "    or (to_user_id = (select id from user_users_profiles where user_id = ?1)) " +
            "    UNION " +
            "    SELECT to_user_id AS user_id " +
            "    FROM user_friend_requests " +
            "    WHERE status = true " +
            "    and (from_user_id = (select id from user_users_profiles where user_id = ?1)) " +
            "    or (to_user_id = (select id from user_users_profiles where user_id = ?1)) " +
            ") AS combined_users " +
            "ORDER BY user_id asc;", nativeQuery = true)
    public List<Long> findDistinctIdsFriendsByUserId(Long idUser);
}
