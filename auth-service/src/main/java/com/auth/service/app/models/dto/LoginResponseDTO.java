package com.auth.service.app.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {

    private Long id;
    private String username;
    private String email;
    private List<RoleDTO> roles;
//    private Collection<? extends GrantedAuthority> authorities;
    private String accessToken;
    private String refreshToken;
    private String type = "Bearer";
    private LocalDateTime currentDateTime;
    private Boolean firstLogin;
}
