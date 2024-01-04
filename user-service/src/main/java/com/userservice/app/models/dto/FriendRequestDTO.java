package com.userservice.app.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nrt.common.microservice.models.dto.CommonDTO;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendRequestDTO extends CommonDTO {

    @NotNull
    private UserProfileDTO fromUser;
    @NotNull
    @Email
    private String fromEmail;
    @NotNull
    private UserProfileDTO toUser;
    @NotNull
    @Email
    private String toEmail;
    private Boolean status;
    private LocalDateTime createAt;
}
