package com.userservice.app.models.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nrt.common.microservice.config.PointSerializer;
import nrt.common.microservice.models.dto.CommonDTO;
import org.locationtech.jts.geom.Point;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDTO extends CommonDTO {

    @NotBlank
    private String city;
    @NotBlank
    private String state;
    @NotBlank
    private String country;
    @NotBlank
    private String postalCode;
    @NotBlank
    private String address;
    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;
    @NotNull
    private UserProfileDTO userProfileDTO;
    @JsonSerialize(using = PointSerializer.class)
    private Point coordinate;
}
