package com.userservice.app.services.impl;

import com.userservice.app.error.ErrorCode;
import com.userservice.app.models.dto.FriendRequestDTO;
import com.userservice.app.models.dto.UserProfileDTO;
import com.userservice.app.models.entity.FriendRequest;
import com.userservice.app.models.repository.FriendRequestRepository;
import com.userservice.app.services.FriendRequestService;
import com.userservice.app.services.UserProfileService;
import lombok.extern.slf4j.Slf4j;
import nrt.common.microservice.exceptions.CommonBusinessException;
import nrt.common.microservice.models.repository.CommonEntityRepository;
import nrt.common.microservice.security.dto.CommonUserDetails;
import nrt.common.microservice.security.session.AppSessionUser;
import nrt.common.microservice.services.impl.CommonServiceImpl;
import nrt.common.microservice.utils.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FriendRequestServiceImpl extends CommonServiceImpl<FriendRequestDTO, FriendRequest>
        implements FriendRequestService {

    @Autowired
    private FriendRequestRepository friendRequestRepository;
    @Autowired
    private UserProfileService userProfileService;

    @Override
    protected CommonEntityRepository<FriendRequest> getCommonRepository() {
        return this.friendRequestRepository;
    }

    @Override
    protected Specification<FriendRequest> getCustomSpecification(Object filter) {
        return null;
    }

    @Override
    protected FriendRequest dtoToEntity(FriendRequestDTO dto) {
        log.info("Enter to dtoToEntity()");
        FriendRequest friendRequest = FriendRequest.builder()
                .fromUser(this.userProfileService.dtoToEntity(dto.getFromUser()))
                .fromEmail(dto.getFromEmail())
                .toUser(this.userProfileService.dtoToEntity(dto.getToUser()))
                .toEmail(dto.getToEmail())
                .status(dto.getStatus()).build();
        friendRequest.setId(dto.getId());
        return friendRequest;
    }

    @Override
    protected FriendRequestDTO entityToDto(FriendRequest entity) {
        log.info("Enter to entityToDto()");
        FriendRequestDTO friendRequestDTO = FriendRequestDTO.builder()
                .fromUser(this.userProfileService.entityToDto(entity.getFromUser()))
                .fromEmail(entity.getFromEmail())
                .toUser(this.userProfileService.entityToDto(entity.getToUser()))
                .toEmail(entity.getToEmail())
                .status(entity.getStatus())
                .createAt(entity.getCreationDateTime()).build();
        friendRequestDTO.setId(entity.getId());
        return friendRequestDTO;
    }

    @Override
    public FriendRequestDTO save(FriendRequestDTO dto) throws Exception {
        log.info("Enter to save()");
        CommonUserDetails userDetails = AppSessionUser.getCurrentAppUser();
        if (!dto.getFromUser().getUserId().equals(userDetails.getId())) {
            throw new CommonBusinessException(ErrorCode.DENIED_ACCESS_PROFILE);
        }

        if (!dto.getFromUser().getEmail().equals(dto.getFromEmail())) {
            throw new CommonBusinessException(ErrorCode.FROM_USER_INVALID);
        }

        if (!dto.getToUser().getEmail().equals(dto.getToEmail())) {
            throw new CommonBusinessException(ErrorCode.TO_USER_INVALID);
        }

        // Validate if the users are previously friends
        List<FriendRequest> previosTrueFriendRequests = this.friendRequestRepository
                .findPreviousFriendsRequestsWithStatusTrue(dto.getFromUser().getId(), dto.getToUser().getId());
        if (!ListUtils.isNullOrEmpty(previosTrueFriendRequests)) {
            throw new CommonBusinessException(ErrorCode.ALREADY_FRIENDS);
        }

        // Validate if the users have previously friends requests
        List<FriendRequest> previousFalseFriendRequests = this.friendRequestRepository
                .findPreviousFriendsRequestsWithStatusFalse(dto.getFromUser().getId(), dto.getToUser().getId());
        if (!ListUtils.isNullOrEmpty(previousFalseFriendRequests)) {
            throw new CommonBusinessException(ErrorCode.REQUEST_HAS_ALREADY_BEEN_SUBMITTED);
        }

        // Validate if the toUser exists
        UserProfileDTO userProfileDTO = this.userProfileService.findById(dto.getToUser().getId());
        if (userProfileDTO == null) {
            throw new CommonBusinessException(ErrorCode.TO_USER_NOT_EXISTS);
        }

        return super.save(dto);
    }

    @Override
    public List<FriendRequestDTO> getLogingUserPendingFriendRequests() {
        log.info("Enter to getLogingUserPendingFriendRequests()");
        CommonUserDetails userDetails = AppSessionUser.getCurrentAppUser();
        List<FriendRequest> friendRequestList = this.friendRequestRepository
                .findPendingFriendsRequestsByUserId(userDetails.getId());

        return friendRequestList.stream().map(this::entityToDto).toList();
    }

    @Override
    public Boolean acceptFriendRequest(Long id) {
        log.info("Enter to acceptFriendRequest()");
        Optional<FriendRequest> friendRequestDb = this.friendRequestRepository.findById(id);
        if (friendRequestDb.isEmpty()) {
            throw new CommonBusinessException(ErrorCode.NOT_EXISTS_FRIEND_REQUEST);
        }
        FriendRequest fr = friendRequestDb.get();
        fr.setStatus(Boolean.TRUE);
        this.friendRequestRepository.save(fr);
        return Boolean.TRUE;
    }

    @Override
    public void declineFriendRequest(Long id) {
        log.info("Enter to declineFriendRequest()");
        Optional<FriendRequest> friendRequestDb = this.friendRequestRepository.findById(id);
        if (friendRequestDb.isEmpty()) {
            throw new CommonBusinessException(ErrorCode.NOT_EXISTS_FRIEND_REQUEST);
        }
        this.friendRequestRepository.delete(friendRequestDb.get());
    }

    @Override
    public List<Long> getFriendsIdsByUser() {
        log.info("Enter to getFriendsIdsByUsers()");
        CommonUserDetails userDetails = AppSessionUser.getCurrentAppUser();
        return this.friendRequestRepository.findDistinctIdsFriendsByUserId(userDetails.getId());
    }

}
