package com.auth.service.app.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nrt.common.microservice.models.dto.CommonDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDTO extends CommonDTO {

    private String roleName;
}
