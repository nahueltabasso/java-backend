package com.auth.service.app.controllers;

import com.auth.service.app.models.dto.PasswordRequestDTO;
import com.auth.service.app.services.PasswordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/security/password")
@Slf4j
public class PasswordController {

    @Autowired
    private PasswordService passwordService;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") String email) {
        log.info("Enter to forgotPassword()");
        log.info("Email user request to change password -> " + email);
        passwordService.requestPasswordChange(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordRequestDTO dto) {
        log.info("Enter to resetPassword()");
        passwordService.resetPassword(dto);
        return ResponseEntity.ok().build();
    }

}
