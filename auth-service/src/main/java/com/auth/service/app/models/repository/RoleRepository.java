package com.auth.service.app.models.repository;

import com.auth.service.app.models.entity.Role;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends PagingAndSortingRepository<Role, Long> {

    public Role findByRoleName(String roleName);
}
