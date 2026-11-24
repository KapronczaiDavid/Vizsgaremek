package com.cleanride.cleanride.service;

import com.cleanride.cleanride.entity.Users;
import com.cleanride.cleanride.repository.EmailVerificationRepository;
import com.cleanride.cleanride.repository.EmailVerificationRepository.ActiveToken;
import com.cleanride.cleanride.repository.EmailVerificationRepository.SendStats;
import com.cleanride.cleanride.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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

/**
 * Email verification with a 6-digit code.
 *
 * Rules:
 *  - a new code invalidates every older code of the user
 *  - max 5 attempts per code (consumed atomically in the DB)
 *  - 60 s cooldown between sends
 *  - max 5 sends per hour per account (the registration email counts as the first)
 *
 * All timestamps are UTC and generated in Java (see the note in the SQL file).
 */
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final EmailVerificationRepository tokens;
    private final UsersRepository users;
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

    /** Called right after registration. */
    @Transactional
    public void sendInitialCode(Users user) {
        issueCode(user, utcNow());
    }

    /**
     * Resend. Unknown or already verified emails are silently ignored (204),
     * so this endpoint does not tell anyone more than registration already does.
     */
    @Transactional(noRollbackFor = ResponseStatusException.class)
    public void resend(String email) {
        Optional<Users> found = users.getUserByEmail(email);
        if (found.isEmpty() || Boolean.TRUE.equals(found.get().getEmailVerified())) {
            return;
        }
        Users user = found.get();
        LocalDateTime now = utcNow();

        SendStats stats = tokens.getSendStats(
                user.getId(), now.minusHours(1), now.minusSeconds(cooldownSeconds));

        if (stats.sentInCooldown() > 0) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Várj legalább " + cooldownSeconds + " másodpercet az új kód kérése előtt");
        }
        if (stats.sentLastHour() >= maxSendsPerHour) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Túl sok kódot kértél, próbáld újra egy óra múlva");
        }
        issueCode(user, now);
    }

    /**
     * IMPORTANT: noRollbackFor is required. Every wrong guess throws a
     * ResponseStatusException; without it the transaction would roll back and
     * the consumed attempt would be lost, i.e. unlimited guesses.
     */
    @Transactional(noRollbackFor = ResponseStatusException.class)
    public void verify(String email, String code) {
        Users user = users.getUserByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Hibás vagy lejárt kód"));

        if (Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ez az email cím már meg van erősítve");
        }

        LocalDateTime now = utcNow();
        ActiveToken token = tokens.getActive(user.getId(), now)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "A kód lejárt vagy nem létezik, kérj új kódot"));

        // Count the attempt BEFORE comparing, atomically: attempts < max is checked in SQL.
        if (!tokens.consumeAttempt(token.id(), maxAttempts)) {
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

        if (!tokens.markUsed(token.id(), now)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A kód már fel lett használva");
        }
        users.markEmailVerified(user.getId());
    }

    private void issueCode(Users user, LocalDateTime now) {
        tokens.invalidateAll(user.getId(), now);

        String code = String.format("%06d", RANDOM.nextInt(1_000_000));
        tokens.createToken(user.getId(), hash(user.getId(), code),
                now.plusMinutes(codeTtlMinutes), now);

        mailService.sendVerificationCode(user.getEmail(), user.getName(), code, codeTtlMinutes);
    }

    /**
     * HMAC-SHA256 with a server secret. A plain SHA-256 of a 6-digit code could be
     * reversed in a second from a DB dump; with the secret it cannot.
     * The user id is part of the input so equal codes do not give equal hashes.
     */
    private String hash(Integer userId, String code) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(hmacSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(
                    mac.doFinal((userId + ":" + code).getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    private LocalDateTime utcNow() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }
}
