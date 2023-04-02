package com.auth.service.app.models.repository;

import com.auth.service.app.models.entity.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {

    public PasswordReset findByCode(String code);

    @Modifying
    @Query(value = "DELETE FROM auth_password_reset WHERE user_id = ?1", nativeQuery = true)
    @Transactional
    public int deleteByUserId(Long userId);
}
