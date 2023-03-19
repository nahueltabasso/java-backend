package com.auth.service.app.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordRequestDTO {

    private String code;
    private String newPassword;
    private String confirmPassword;
}
