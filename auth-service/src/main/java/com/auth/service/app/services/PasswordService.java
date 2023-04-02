package com.auth.service.app.services;

import com.auth.service.app.models.dto.PasswordRequestDTO;

public interface PasswordService {

    public void requestPasswordChange(String email);
    public void resetPassword(PasswordRequestDTO dto);
    public void deleteByUserId(Long userId);
}
