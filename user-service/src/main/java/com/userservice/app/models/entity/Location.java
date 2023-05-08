package com.userservice.app.models.entity;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nrt.common.microservice.config.PointSerializer;
import nrt.common.microservice.models.entity.CommonEntity;
import org.locationtech.jts.geom.Point;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_locations")
public class Location extends CommonEntity {

    @Column(name = "city")
    @NotBlank
    private String city;
    @Column(name = "state")
    @NotBlank
    private String state;
    @Column(name = "country")
    @NotBlank
    private String country;
    @Column(name = "postal_code")
    @NotBlank
    private String postalCode;
    @Column(name = "address")
    @NotBlank
    private String address;
    @Column(name = "latitude")
    @NotNull
    private Double latitude;
    @Column(name = "longitude")
    @NotNull
    private Double longitude;
    @ManyToOne
    @JoinColumn(name = "user_profile_id")
    private UserProfile userProfile;
    @Column(name = "coordinate", columnDefinition = "POINT")
    @NotNull
    @JsonSerialize(using = PointSerializer.class)
    private Point coordinate;
}
