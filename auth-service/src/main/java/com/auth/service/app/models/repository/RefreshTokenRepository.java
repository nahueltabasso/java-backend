package com.auth.service.app.models.repository;

import com.auth.service.app.models.entity.RefreshToken;
import com.auth.service.app.models.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    public Optional<RefreshToken> findById(Long id);

    public Optional<RefreshToken> findByToken(String token);

    @Modifying
    public int deleteByUser(User use);

}
