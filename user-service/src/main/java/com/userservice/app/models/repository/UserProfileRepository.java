package com.userservice.app.models.repository;

import com.userservice.app.models.entity.UserProfile;
import nrt.common.microservice.models.repository.CommonEntityRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends CommonEntityRepository<UserProfile> {

    @Query(value = "select email from auth_users where username = ?1", nativeQuery = true)
    public String findEmailUserByUsername(String username);
}
