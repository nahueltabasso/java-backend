package com.auth.service.app.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nrt.common.microservice.models.dto.BaseEmail;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthEmailDTO extends BaseEmail {

    private String username;
    private String body;

}
