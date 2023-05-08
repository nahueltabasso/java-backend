package com.userservice.app.models.repository;

import com.userservice.app.models.entity.Location;
import nrt.common.microservice.models.repository.CommonEntityRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface LocationRepository extends CommonEntityRepository<Location> {

    @Modifying
    @Query(value = "DELETE FROM user_locations WHERE user_profile_id = ?1", nativeQuery = true)
    @Transactional
    public int deleteByUserProfileId(Long userProfileId);
}
