package com.userservice.app.controller;

import com.userservice.app.error.ErrorCode;
import com.userservice.app.models.dto.UserProfileDTO;
import com.userservice.app.models.dto.UserProfileFilterDTO;
import com.userservice.app.services.UserProfileService;
import lombok.extern.slf4j.Slf4j;
import nrt.common.microservice.controllers.CommonController;
import nrt.common.microservice.exceptions.CommonBusinessException;
import nrt.common.microservice.security.dto.CommonUserDetails;
import nrt.common.microservice.security.session.AppSessionUser;
import nrt.common.microservice.services.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users/user-profile")
@Slf4j
public class UserProfileController extends CommonController<UserProfileFilterDTO, UserProfileDTO> {

    @Autowired
    private UserProfileService userProfileService;

    @Override
    protected CommonService getCommonService() {
        return this.userProfileService;
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/message-ok")
    public String getMessage(@RequestHeader("Authorization") String token){
        CommonUserDetails commonUserDetails = AppSessionUser.getCurrentAppUser();
        return "oK";
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/add")
    public ResponseEntity<?> addProfile(@Validated @RequestPart("jsonBody") UserProfileDTO userProfileDTO,
                                        BindingResult bindingResult, @RequestPart("profilePhoto") MultipartFile file) {
        log.info("Enter to addProfile");

        if (bindingResult.hasErrors()) {
            return this.validateBody(bindingResult);
        }
        if (file.isEmpty()) {
            throw new CommonBusinessException(ErrorCode.FILE_NOT_NULL);
        }

        // Valid if the file is a valid image such as a profile photo picture
        userProfileService.validFaceInProfilePotho(file);

        log.info("Passes body request validations");
        log.info("Content-Type of request -> " + file.getContentType());
        log.info("Filename -> " + file.getOriginalFilename());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userProfileService.saveNewProfile(userProfileDTO, file));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/update-profile/{id}")
    public ResponseEntity<?> updateProfile(@Validated @RequestPart("jsonBody") UserProfileDTO userProfileDTO,
                                           BindingResult bindingResult, @RequestPart("profilePhoto") MultipartFile file,
                                           @PathVariable Long id) {
        log.info("Enter to updateProfile()");

        if (bindingResult.hasErrors()) {
            return this.validateBody(bindingResult);
        }
        CommonUserDetails userDetails = AppSessionUser.getCurrentAppUser();
        log.info("Passes body request validation");
        log.info("User -> " + userDetails.getUsername());

        UserProfileDTO userProfileDTOResponse = userProfileService.updateProfile(id, userProfileDTO, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(userProfileDTOResponse);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/disabled/{id}")
    public ResponseEntity<?> disabledProfile(@PathVariable Long id) {
        log.info("Enter to disabledProfile()");
        userProfileService.disabledUserProfile(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/suggest-possible-friends/{id}")
    public ResponseEntity<?> getSuggestionsPossiblesNewFriends(@PathVariable Long id) {
        log.info("Enter to getSuggestionsPossiblesNewFriends()");
        return ResponseEntity.ok()
                .body(this.userProfileService.getPossibleNewFriends(id));
    }

}
