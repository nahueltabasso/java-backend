package com.userservice.app.models.repository;

import com.userservice.app.models.entity.UserProfile;
import nrt.common.microservice.models.repository.CommonEntityRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProfileRepository extends CommonEntityRepository<UserProfile> {

    @Query(value = "select email from auth_users where username = ?1", nativeQuery = true)
    public String findEmailUserByUsername(String username);

    public UserProfile findByEmail(String email);

    @Query(value = "SELECT profile.*" +
            "FROM user_users_profiles profile " +
            "INNER JOIN user_locations location ON location.user_profile_id = profile.id " +
            "WHERE location.user_profile_id != ?1 " +
            "AND ST_Distance_Sphere(location.coordinate, " +
            "         (SELECT coordinate FROM user_locations WHERE user_profile_id = ?1)) <= 50000", nativeQuery = true)
    public List<UserProfile> findNearestUserProfilesByUserProfileId(Long id);

    @Query("SELECT up FROM UserProfile up WHERE up.id <> ?1")
    public List<UserProfile> findFirstTenUserProfiles(Long id, Pageable pageable);

    public UserProfile findByUserId(Long userId);
}
