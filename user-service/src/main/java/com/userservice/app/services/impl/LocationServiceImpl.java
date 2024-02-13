package com.userservice.app.services.impl;

import com.userservice.app.error.ErrorCode;
import com.userservice.app.models.dto.LocationDTO;
import com.userservice.app.models.dto.UserProfileDTO;
import com.userservice.app.models.entity.Location;
import com.userservice.app.models.entity.UserProfile;
import com.userservice.app.models.repository.LocationRepository;
import com.userservice.app.services.LocationService;
import com.userservice.app.services.UserProfileService;
import lombok.extern.slf4j.Slf4j;
import nrt.common.microservice.exceptions.CommonBusinessException;
import nrt.common.microservice.models.repository.CommonEntityRepository;
import nrt.common.microservice.security.session.AppSessionUser;
import nrt.common.microservice.services.impl.CommonServiceImpl;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LocationServiceImpl extends CommonServiceImpl<LocationDTO, Location> implements LocationService {

    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    @Lazy
    private UserProfileService userProfileService;

    @Override
    protected CommonEntityRepository<Location> getCommonRepository() {
        return this.locationRepository;
    }

    @Override
    protected Specification<Location> getCustomSpecification(Object filter) {
        return null;
    }

    @Override
    protected Location dtoToEntity(LocationDTO dto) {
        log.info("Enter to dtoToEntity()");
        UserProfile userProfile = new UserProfile();
        BeanUtils.copyProperties(dto.getUserProfileDTO(), userProfile);

        Point coordinate = this.getCoordinatePoint(dto.getLatitude(), dto.getLongitude());

        Location entity = Location.builder()
                .city(dto.getCity())
                .state(dto.getState())
                .country(dto.getCountry())
                .postalCode(dto.getPostalCode())
                .address(dto.getAddress())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .userProfile(userProfile)
                .coordinate(coordinate).build();
        return entity;
    }

    @Override
    protected LocationDTO entityToDto(Location entity) {
        log.info("Enter to entityToDto()");
        UserProfileDTO userProfileDTO = this.userProfileService.findById(entity.getUserProfile().getId());
//        BeanUtils.copyProperties(entity.getUserProfile(), userProfileDTO);

        LocationDTO dto = LocationDTO.builder()
                .city(entity.getCity())
                .state(entity.getState())
                .country(entity.getCountry())
                .postalCode(entity.getPostalCode())
                .address(entity.getAddress())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .userProfileDTO(userProfileDTO)
                .coordinate(entity.getCoordinate()).build();
        dto.setId(entity.getId());
        return dto;
    }

    @Override
    public LocationDTO save(LocationDTO dto) throws Exception {
        log.info("Enter to save()");
        // Validate that the user id is the same as the logged in user id
        Long loggedUserId = AppSessionUser.getCurrentAppUser().getId();
        if (!loggedUserId.equals(dto.getUserProfileDTO().getUserId())) {
            throw new CommonBusinessException(ErrorCode.DENIED_ACCESS_PROFILE);
        }

        UserProfileDTO userProfileDTO = userProfileService.findById(dto.getUserProfileDTO().getId());
        if (userProfileDTO == null) {
            throw new CommonBusinessException(ErrorCode.NOT_FOUND_USER_PROFILE);
        }

        if (dto.getLatitude().isNaN() || dto.getLongitude().isNaN()) {
            throw new CommonBusinessException(ErrorCode.COORDINATE_NOT_NULL);
        }

        Location location = this.dtoToEntity(dto);
        location = locationRepository.save(location);
        return this.entityToDto(location);
    }

    @Override
    public void deleteLocationByUserProfile(Long userProfileId) {
        log.info("Enter to deleteLocationByUserProfile()");
        locationRepository.deleteByUserProfileId(userProfileId);
    }
}
