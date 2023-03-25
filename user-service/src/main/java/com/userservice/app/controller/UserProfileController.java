package com.userservice.app.controller;

import nrt.common.microservice.security.dto.CommonUserDetails;
import nrt.common.microservice.security.session.AppSessionUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/user-profile")
public class UserProfileController {

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/message-ok")
    public String getMessage(@RequestHeader("Authorization") String token){
        CommonUserDetails commonUserDetails = AppSessionUser.getCurrentAppUser();
        return "oK";
    }
}
