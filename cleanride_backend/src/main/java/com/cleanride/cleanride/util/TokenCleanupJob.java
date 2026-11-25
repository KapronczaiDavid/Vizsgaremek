package com.cleanride.cleanride.util;

import com.cleanride.cleanride.service.AuthTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenCleanupJob {

    private final AuthTokenService authTokenService;

    @Scheduled(cron = "0 30 3 * * *")
    public void purgeExpiredTokens() {
        authTokenService.purgeExpired();
    }
}
