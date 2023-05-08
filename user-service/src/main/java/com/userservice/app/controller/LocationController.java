package com.userservice.app.controller;

import com.userservice.app.models.dto.LocationDTO;
import com.userservice.app.models.dto.LocationFilterDTO;
import com.userservice.app.services.LocationService;
import nrt.common.microservice.controllers.CommonController;
import nrt.common.microservice.services.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/location")
public class LocationController extends CommonController<LocationFilterDTO, LocationDTO> {

    @Autowired
    private LocationService locationService;

    @Override
    protected CommonService getCommonService() {
        return this.locationService;
    }
}
