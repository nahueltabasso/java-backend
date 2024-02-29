package com.userservice.app.services.impl;

import com.userservice.app.clients.AuthFeignClient;
import com.userservice.app.clients.DetectFaceApiClient;
import com.userservice.app.error.ErrorCode;
import com.userservice.app.models.dto.DetectFaceAPIResponseDTO;
import com.userservice.app.models.dto.UserProfileDTO;
import com.userservice.app.models.entity.UserProfile;
import com.userservice.app.models.repository.UserProfileRepository;
import com.userservice.app.services.LocationService;
import com.userservice.app.services.UserProfileService;
import helpers.CloudinaryHelper;
import lombok.extern.slf4j.Slf4j;
import nrt.common.microservice.exceptions.CommonBusinessException;
import nrt.common.microservice.exceptions.ResourceNotFoundException;
import nrt.common.microservice.models.repository.CommonEntityRepository;
import nrt.common.microservice.security.dto.CommonUserDetails;
import nrt.common.microservice.security.session.AppSessionUser;
import nrt.common.microservice.helpers.FileHelper;
import nrt.common.microservice.services.impl.CommonServiceImpl;
import nrt.common.microservice.utils.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserProfileServiceImpl extends CommonServiceImpl<UserProfileDTO, UserProfile> implements UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    @Lazy
    private LocationService locationService;
    @Autowired
    private AuthFeignClient authFeignClient;
    @Autowired
    private FileHelper fileHelper;
    @Autowired
    private CloudinaryHelper cloudinaryHelper;
    @Autowired
    private Environment environment;
    @Value("${cloudinary.active}")
    private Boolean saveInCDN;
    @Value("${cloudinary.host}")
    private String cloudinaryHost;
    @Autowired
    private DetectFaceApiClient detectFaceApiClient;


    @Override
    protected CommonEntityRepository<UserProfile> getCommonRepository() {
        return this.userProfileRepository;
    }

    @Override
    protected Specification<UserProfile> getCustomSpecification(Object filter) {
        return null;
    }

    @Override
    public UserProfile dtoToEntity(UserProfileDTO dto) {
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
    public UserProfileDTO entityToDto(UserProfile entity) {
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

        UserProfile userProfileExist = userProfileRepository.findByEmail(email);
        if (userProfileExist != null) {
            throw new CommonBusinessException(ErrorCode.EXIST_PROFILE_USER);
        }

        // TODO: Save the profile photo in a directory or cloudinary
        if (saveInCDN) {
            // TODO: call the method to save a file in cloudinary
            String imageUrl = saveInCloudinary(profilePhoto);
            imageUrl = getImagePathWithCloudinaryTransformations(imageUrl);
            dto.setProfilePhoto(imageUrl);
        } else {
            String directory = environment.getProperty("paths.common-image-directory.location");
            dto.setProfilePhoto(fileHelper.saveImageInDirectory(profilePhoto, directory));
        }
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
            if (saveInCDN) {
                // TODO: call the method to save a file in cloudinary
                String imageUrl = saveInCloudinary(profilePhoto);
                imageUrl = getImagePathWithCloudinaryTransformations(imageUrl);
                dto.setProfilePhoto(imageUrl);
            } else {
                String directory = environment.getProperty("paths.common-image-directory.location");
                dto.setProfilePhoto(fileHelper.saveImageInDirectory(profilePhoto, directory));
            }
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

        deleteProfilePhoto(userProfile);
        log.info("Delete file in " + userProfile.getProfilePhoto());

        // Delete the locations of user profile
        locationService.deleteLocationByUserProfile(id);

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

    @Override
    public void validFaceInProfilePotho(MultipartFile file) {
        log.info("Enter to validFaceInProfilePhoto");
        String apiKey = environment.getProperty("detectFaceApi.api_key");
        DetectFaceAPIResponseDTO responseDTO = detectFaceApiClient.detectFaces(apiKey, file);
        String errorCode = responseDTO.getErrorCode();
        if (errorCode != null && !errorCode.isEmpty()) {
            throw new CommonBusinessException(errorCode);
        }

        if (!responseDTO.isDetectedFace()) {
            throw new CommonBusinessException(ErrorCode.PROFILE_PHOTO_NOT_VALID);
        }

        if (responseDTO.getNumberOfDetectedFaces() > 1) {
            throw new CommonBusinessException(ErrorCode.PROFILE_PHOTO_DETECT_MANY_FACES);
        }
    }

    @Override
    public List<UserProfileDTO> getPossibleNewFriends(Long userProfileId) {
        log.info("Enter to getPossibleNewFriends()");
        List<UserProfile> userProfileList = this.userProfileRepository.findNearestUserProfilesByUserProfileId(userProfileId);

        // Validate if there is any nearby user to suggest as a friend
        if (ListUtils.isNullOrEmpty(userProfileList)) {
            // There aren't nearby users so suggest 10 random users
            userProfileList = this.userProfileRepository.findFirstTenUserProfiles(userProfileId, PageRequest.of(0, 10));
        }

        return userProfileList.stream()
                .map(up -> this.entityToDto(up))
                .collect(Collectors.toList());
    }

    private String saveInCloudinary(MultipartFile multipartFile) {
        File file = fileHelper.convert(multipartFile);
        String imageUrl = cloudinaryHelper.uploadImage(file);
        if (imageUrl.isEmpty()) {
            throw new CommonBusinessException(ErrorCode.ERROR_WITH_CLOUDINARY);
        }
        return imageUrl;
    }

    private void deleteProfilePhoto(UserProfile userProfile) {
        if (userProfile.getProfilePhoto().startsWith(cloudinaryHost)) {
            String result = cloudinaryHelper.destroyImage(userProfile.getProfilePhoto());
            if (!result.equalsIgnoreCase("200"))
                throw new CommonBusinessException(ErrorCode.ERROR_WITH_CLOUDINARY);
            return ;
        }
        if (!fileHelper.deleteFileByPath(userProfile.getProfilePhoto())) {
            log.error("Can not delete file");
            throw new CommonBusinessException(ErrorCode.CANNOT_DELETE_FILE);
        }
    }

    private String getImagePathWithCloudinaryTransformations(String imageUrl) {
        Map<String, String> transformations = new HashMap<>();
        transformations.put("crop", "fill");
        transformations.put("width", "400");
        transformations.put("height", "400");
        transformations.put("gravity", "face");
        transformations.put("radius", "max");

        String transformedImageUrl = cloudinaryHelper.applyImageTransformations(imageUrl, transformations);
        return transformedImageUrl;
    }

}
