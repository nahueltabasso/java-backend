package com.userservice.app.services.impl;

import com.userservice.app.clients.AuthFeignClient;
import com.userservice.app.error.ErrorCode;
import com.userservice.app.models.dto.UserProfileDTO;
import com.userservice.app.models.entity.UserProfile;
import com.userservice.app.models.repository.UserProfileRepository;
import com.userservice.app.services.UserProfileService;
import lombok.extern.slf4j.Slf4j;
import nrt.common.microservice.exceptions.CommonBusinessException;
import nrt.common.microservice.exceptions.ResourceNotFoundException;
import nrt.common.microservice.models.repository.CommonEntityRepository;
import nrt.common.microservice.security.dto.CommonUserDetails;
import nrt.common.microservice.security.session.AppSessionUser;
import nrt.common.microservice.services.impl.CommonServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class UserProfileServiceImpl extends CommonServiceImpl<UserProfileDTO, UserProfile> implements UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private AuthFeignClient authFeignClient;

    @Override
    protected CommonEntityRepository<UserProfile> getCommonRepository() {
        return this.userProfileRepository;
    }

    @Override
    protected Specification<UserProfile> getCustomSpecification(Object filter) {
        return null;
    }

    @Override
    protected UserProfile dtoToEntity(UserProfileDTO dto) {
        log.info("Enter to dtoToEntity()");
        UserProfile entity = UserProfile.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .profilePhoto(dto.getProfilePhoto())
                .birthDate(dto.getBirthDate())
                .verifiedProfile(dto.getVerifiedProfile())
                .personalStatus(dto.getPersonalStatus())
                .studies(dto.getStudies())
                .biography(dto.getBiography())
                .userId(dto.getUserId())
                .activeProfile(dto.getActiveProfile()).build();
        entity.setId(dto.getId());
        return entity;
    }

    @Override
    protected UserProfileDTO entityToDto(UserProfile entity) {
        UserProfileDTO dto = UserProfileDTO.builder()
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .profilePhoto(entity.getProfilePhoto())
                .birthDate(entity.getBirthDate())
                .verifiedProfile(entity.getVerifiedProfile())
                .personalStatus(entity.getPersonalStatus())
                .studies(entity.getStudies())
                .biography(entity.getBiography())
                .userId(entity.getUserId())
                .activeProfile(entity.getActiveProfile()).build();
        dto.setId(entity.getId());
        return dto;
    }

    @Override
    public UserProfileDTO saveNewProfile(UserProfileDTO dto, MultipartFile profilePhoto) {
        log.info("Enter to saveNewProfile()");
        // Check if the user of the new profile is the logged user
        CommonUserDetails userDetails = AppSessionUser.getCurrentAppUser();
        if (!dto.getUserId().equals(userDetails.getId())) {
            throw new CommonBusinessException(ErrorCode.DENIED_ACCESS_PROFILE);
        }

        String email = userProfileRepository.findEmailUserByUsername(userDetails.getUsername());
        if (email.isEmpty() || !email.equalsIgnoreCase(dto.getEmail())) {
            throw new CommonBusinessException(ErrorCode.EMAIL_NOT_MATCH_USER_EMAIL);
        }
        // TODO: Save the profile photo in a directory or cloudinary
        dto.setProfilePhoto(profilePhoto.getOriginalFilename());
        // TODO: Transform the dto object to entity object and persist in database
        UserProfile userProfile = dtoToEntity(dto);
        userProfile = userProfileRepository.save(userProfile);
        return entityToDto(userProfile);
    }

    @Override
    public UserProfileDTO updateProfile(Long id, UserProfileDTO dto, MultipartFile profilePhoto) {
        log.info("Enter to updateProfile()");
        UserProfile userProfile = userProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.NOT_FOUND_USER_PROFILE));

        // Valid if the user id of data is equal to user id from database
        if (!dto.getUserId().equals(AppSessionUser.getCurrentAppUser().getId()))
            throw new CommonBusinessException(ErrorCode.DENIED_ACCESS_PROFILE);

        if (!profilePhoto.isEmpty()) {
            // TODO: Save the new profile photo in a directory or cloudinary
            dto.setProfilePhoto(profilePhoto.getOriginalFilename());
        }

        userProfile.setId(dto.getId());
        userProfile.setFirstName(dto.getFirstName());
        userProfile.setLastName(dto.getLastName());
        userProfile.setEmail(dto.getEmail());
        userProfile.setPhoneNumber(dto.getPhoneNumber());
        userProfile.setProfilePhoto(dto.getProfilePhoto());
        userProfile.setBirthDate(dto.getBirthDate());
        userProfile.setVerifiedProfile(dto.getVerifiedProfile());
        userProfile.setPersonalStatus(dto.getPersonalStatus());
        userProfile.setStudies(dto.getStudies());
        userProfile.setBiography(dto.getBiography());
        userProfile.setUserId(dto.getUserId());
        userProfile.setActiveProfile(dto.getActiveProfile());
        userProfile = userProfileRepository.save(userProfile);
        return entityToDto(userProfile);
    }

    @Override
    public void deleteById(Long id) {
        log.info("Enter to deleteById()");
        UserProfile userProfile = userProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.NOT_FOUND_USER_PROFILE));
        log.info("Profile exists");

        userProfileRepository.deleteById(id);
        log.info("Deleted profile for user with email -> " + userProfile.getEmail());
        log.info("Deleted profile for user with username -> " + AppSessionUser.getCurrentAppUser().getUsername());

        // TODO: delete the user in the table auth_users with feign client to auth-service
        CommonUserDetails userDetails = AppSessionUser.getCurrentAppUser();
        authFeignClient.deleteUser("Bearer " + userDetails.getToken(), userProfile.getUserId());
        log.info("Deleted user successfully -> " + userDetails.getUsername());
    }

    @Override
    public void disabledUserProfile(Long id) {
        log.info("Enter to disableUserProfile()");
        UserProfile userProfile = userProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.NOT_FOUND_USER_PROFILE));
        log.info("Profile exists");
        log.info("Proceed to deactivate the profile");
        userProfile.setActiveProfile(Boolean.FALSE);
        userProfileRepository.save(userProfile);
    }
}
