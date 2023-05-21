package com.userservice.app.services;

import com.userservice.app.models.dto.UserProfileDTO;
import nrt.common.microservice.services.CommonService;
import org.springframework.web.multipart.MultipartFile;

public interface UserProfileService extends CommonService<UserProfileDTO> {

    public UserProfileDTO saveNewProfile(UserProfileDTO dto, MultipartFile profilePhoto);
    public UserProfileDTO updateProfile(Long id, UserProfileDTO dto, MultipartFile profilePhoto);
    public void disabledUserProfile(Long id);
    public void validFaceInProfilePotho(MultipartFile file);

}
