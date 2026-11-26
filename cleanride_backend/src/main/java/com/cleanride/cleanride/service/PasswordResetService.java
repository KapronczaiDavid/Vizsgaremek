package com.cleanride.cleanride.service;

import com.cleanride.cleanride.entity.Users;
import com.cleanride.cleanride.repository.AuthTokenRepository;
import com.cleanride.cleanride.repository.PasswordResetRepository;
import com.cleanride.cleanride.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HexFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final PasswordResetRepository tokens;
    private final AuthTokenRepository authTokens;
    private final UsersRepository users;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    @Value("${verification.hmac-secret}")
    private String hmacSecret;
    @Value("${verification.code-ttl-minutes:10}")
    private int codeTtlMinutes;
    @Value("${verification.max-attempts:5}")
    private int maxAttempts;
    @Value("${verification.resend-cooldown-seconds:60}")
    private int cooldownSeconds;
    @Value("${verification.max-sends-per-hour:5}")
    private int maxSendsPerHour;

    /** Always looks successful from outside: no hint whether the e-mail exists or was throttled. */
    @Transactional
    public void requestReset(String email) {
        Optional<Users> found = users.getUserByEmail(email);
        if (found.isEmpty()) {
            return;
        }
        Users user = found.get();
        LocalDateTime now = utcNow();

        PasswordResetRepository.SendStats stats = tokens.getSendStats(
                user.getId(), now.minusHours(1), now.minusSeconds(cooldownSeconds));
        if (stats.sentInCooldown() > 0 || stats.sentLastHour() >= maxSendsPerHour) {
            return;
        }
        issueCode(user, now);
    }

    @Transactional(noRollbackFor = ResponseStatusException.class)
    public void reset(String email, String code, String newPassword) {
        Users user = users.getUserByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Hibás vagy lejárt kód"));

        LocalDateTime now = utcNow();
        PasswordResetRepository.ActiveToken token = tokens.getActive(user.getId(), now)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "A kód lejárt vagy nem létezik, kérj új kódot"));

        if (!authTokens.consumeAttempt(token.id(), maxAttempts)) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Elfogytak a próbálkozások, kérj új kódot");
        }

        boolean matches = MessageDigest.isEqual(
                hash(user.getId(), code).getBytes(StandardCharsets.UTF_8),
                token.tokenHash().getBytes(StandardCharsets.UTF_8));

        if (!matches) {
            int remaining = maxAttempts - (token.attempts() + 1);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    remaining > 0
                            ? "Hibás kód. Hátralévő próbálkozások: " + remaining
                            : "Hibás kód. Elfogytak a próbálkozások, kérj új kódot");
        }

        if (!authTokens.markUsed(token.id(), now)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A kód már fel lett használva");
        }

        users.updatePassword(user.getId(), passwordEncoder.encode(newPassword));
        tokens.invalidateAll(user.getId(), now);       // no other reset code stays usable
        authTokens.revokeAllSessions(user.getId(), now); // log out every device
    }

    private void issueCode(Users user, LocalDateTime now) {
        tokens.invalidateAll(user.getId(), now);

        String code = String.format("%06d", RANDOM.nextInt(1_000_000));
        tokens.createToken(user.getId(), hash(user.getId(), code),
                now.plusMinutes(codeTtlMinutes), now);

        mailService.sendPasswordResetCode(user.getEmail(), user.getName(), code, codeTtlMinutes);
    }

    private String hash(Integer userId, String code) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(hmacSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(
                    mac.doFinal(("reset:" + userId + ":" + code).getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    private LocalDateTime utcNow() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }
}
