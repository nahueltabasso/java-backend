package com.userservice.app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/user-profile")
public class UserProfileController {

    @GetMapping("/message-ok")
    public String getMessage(){

        return "oK";
    }
}
