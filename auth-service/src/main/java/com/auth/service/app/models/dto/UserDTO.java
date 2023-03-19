package com.auth.service.app.models.dto;

import annotations.Password;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nrt.common.microservice.models.dto.CommonDTO;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO extends CommonDTO {

    @NotNull
    @NotEmpty
    private String username;
    @Password
    private String password;
    @NotNull
    @Email
    private String email;
    private Boolean googleUser;
    private Boolean facebookUser;
    private Boolean appleUser;
    private List<RoleDTO> roles;
    private Boolean firstLogin;
    private Boolean userLocked;
    private int failsAttemps;

}
