package com.auth.service.app.models.repository;

import com.auth.service.app.models.entity.User;
import com.auth.service.app.models.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    public List<UserRole> findByUser(User user);
}
