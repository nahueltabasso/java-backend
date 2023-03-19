package com.auth.service.app.services;

import com.auth.service.app.error.ErrorCode;
import com.auth.service.app.models.entity.RefreshToken;
import com.auth.service.app.models.entity.User;
import com.auth.service.app.models.repository.RefreshTokenRepository;
import com.auth.service.app.models.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import nrt.common.microservice.exceptions.CommonBusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Value("${app.security.jwtRefreshExpirationMs}")
    private Long refreshTokenDuration;

    @Override
    public Optional<RefreshToken> getByToken(String token) {
        log.info("Enter to getByToken()");
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken createRefreshToken(Long userId) {
        log.info("Enter to createRefreshToken()");
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new CommonBusinessException(ErrorCode.USER_NOT_FOUND);
        }

        RefreshToken refreshToken =  RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(userOptional.get())
                .expiryDate(Instant.now().plusMillis(refreshTokenDuration)).build();
        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken refreshToken) {
        log.info("Enter to verifyExpiration()");
        if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshToken);
            throw new CommonBusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }
        return refreshToken;
    }

    @Override
    public int deleteByUserId(Long userId) {
        log.info("Enter to deleteByUserId()");
        Optional<User> user = userRepository.findById(userId);
        return refreshTokenRepository.deleteByUser(user.get());
    }
}
