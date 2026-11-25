package com.cleanride.cleanride.service;

import com.cleanride.cleanride.repository.AuthTokenRepository;
import com.cleanride.cleanride.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class AuthTokenService {

    private final AuthTokenRepository authTokenRepository;
    private final JwtService jwtService;

    @Transactional
    public void saveSession(String token, Integer userId, Integer adminId) {
        LocalDateTime expiresAt = LocalDateTime.ofInstant(
                jwtService.parse(token).getExpiration().toInstant(),
                ZoneOffset.UTC);
        authTokenRepository.createToken(userId, adminId, hash(token), expiresAt);
    }
    public boolean isActive(String token) {
        return authTokenRepository.existsValidForLogin(hash(token), LocalDateTime.now(ZoneOffset.UTC));
    }

    @Transactional
    public void purgeExpired() {
        authTokenRepository.purgeExpired(LocalDateTime.now(ZoneOffset.UTC).minusDays(2));
    }

    @Transactional
    public void revoke(String token) {
        authTokenRepository.revokeSession(hash(token), LocalDateTime.now(ZoneOffset.UTC));
    }

    private String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(
                    digest.digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}