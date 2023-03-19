package com.auth.service.app.services;

import com.auth.service.app.models.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {

    public Optional<RefreshToken> getByToken(String token);
    public RefreshToken createRefreshToken(Long userId);
    public RefreshToken verifyExpiration(RefreshToken refreshToken);
    public int deleteByUserId(Long userId);
}
