package com.userservice.app.services;

import com.userservice.app.models.dto.FriendRequestDTO;
import nrt.common.microservice.services.CommonService;

import java.util.List;


public interface FriendRequestService extends CommonService<FriendRequestDTO> {

    public List<FriendRequestDTO> getLogingUserPendingFriendRequests();
    public Boolean acceptFriendRequest(Long id);
    public void declineFriendRequest(Long id);
    public List<Long> getFriendsIdsByUser();

}
