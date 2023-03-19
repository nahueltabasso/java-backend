package com.auth.service.app.models.repository;

import com.auth.service.app.models.entity.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {

    public PasswordReset findByCode(String code);
}
