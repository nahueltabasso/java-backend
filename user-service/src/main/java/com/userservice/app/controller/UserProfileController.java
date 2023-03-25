package com.userservice.app.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/user-profile")
public class UserProfileController {

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/message-ok")
    public String getMessage(@RequestHeader("Authorization") String token){

        return "oK";
    }
}
