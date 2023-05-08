package com.userservice.app.services;

import com.userservice.app.models.dto.LocationDTO;
import nrt.common.microservice.services.CommonService;

public interface LocationService extends CommonService<LocationDTO> {

    public void deleteLocationByUserProfile(Long userProfileId);
}
