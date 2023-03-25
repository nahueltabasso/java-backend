package com.auth.service.app.services;

import com.auth.service.app.models.dto.LoginResponseDTO;
import com.auth.service.app.models.dto.MessageResponseDTO;
import com.auth.service.app.models.dto.UserDTO;
import nrt.common.microservice.services.CommonService;
import org.springframework.security.core.Authentication;

import java.util.Map;

public interface AuthService extends CommonService<UserDTO> {

    public MessageResponseDTO saveNewUser(UserDTO dto);
    public LoginResponseDTO login(Authentication authentication);
    public Boolean checkUserLocked(String username);
    public void updateFailsAttemps(String username);
    public LoginResponseDTO validate(String token);
    public Map<String, Object> getCurrentUserDetails(String token);
}
