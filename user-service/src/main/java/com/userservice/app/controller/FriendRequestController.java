package com.userservice.app.controller;

import com.userservice.app.models.dto.FriendRequestDTO;
import com.userservice.app.models.dto.FriendRequestFilterDTO;
import com.userservice.app.services.FriendRequestService;
import lombok.extern.slf4j.Slf4j;
import nrt.common.microservice.controllers.CommonController;
import nrt.common.microservice.services.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/friend-request")
@Slf4j
public class FriendRequestController extends CommonController<FriendRequestFilterDTO, FriendRequestDTO> {

    @Autowired
    private FriendRequestService friendRequestService;

    @Override
    protected CommonService getCommonService() {
        return this.friendRequestService;
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/pending-friend-requests")
    public ResponseEntity<?> getPendingFriendRequests() {
        log.info("Enter to getPendingFriendRequest()");
        return ResponseEntity.ok().body(this.friendRequestService.getLogingUserPendingFriendRequests());
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/accept/{id}")
    public ResponseEntity<?> acceptFriendRequest(@PathVariable Long id) {
        log.info("Enter to acceptFriendRequest()");
        return ResponseEntity.ok().body(this.friendRequestService.acceptFriendRequest(id));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/decline/{id}")
    public ResponseEntity<?> declineFriendRequest(@PathVariable Long id) {
        log.info("Enter to declineFriendRequest()");
        this.friendRequestService.declineFriendRequest(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/get-friends-ids")
    public ResponseEntity<?> getFriends() {
        log.info("Enter to getFriends()");
        return ResponseEntity.ok().body(this.friendRequestService.getFriendsIdsByUser());
    }
}
