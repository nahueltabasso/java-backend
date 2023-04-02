package com.auth.service.app.models.repository;

import com.auth.service.app.models.entity.User;
import nrt.common.microservice.models.repository.CommonEntityRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CommonEntityRepository<User> {

    public Optional<User> findByUsername(String username);
    public Optional<User> findByEmail(String email);
}
