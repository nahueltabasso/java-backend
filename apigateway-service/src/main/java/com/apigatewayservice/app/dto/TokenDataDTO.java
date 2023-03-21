package com.apigatewayservice.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenDataDTO {

    private Long id;
    private String username;
    private String email;
    private Boolean userEnabled;
    private List<String> authorities;
}
