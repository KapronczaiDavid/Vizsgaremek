package com.cleanride.cleanride.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class EmailVerificationRepository {

    private final EntityManager entityManager;

    public record ActiveToken(Integer id, String tokenHash, int attempts) {}

    public record SendStats(long sentLastHour, long sentInCooldown) {}

    public void createToken(Integer userId, String tokenHash,
                            LocalDateTime expiresAt, LocalDateTime createdAt) {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("create_email_verification_token");
        query.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(2, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(3, LocalDateTime.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(4, LocalDateTime.class, ParameterMode.IN);
        query.setParameter(1, userId);
        query.setParameter(2, tokenHash);
        query.setParameter(3, expiresAt);
        query.setParameter(4, createdAt);
        query.execute();
    }

    public void invalidateAll(Integer userId, LocalDateTime now) {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("invalidate_email_verification_tokens");
        query.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(2, LocalDateTime.class, ParameterMode.IN);
        query.setParameter(1, userId);
        query.setParameter(2, now);
        query.execute();
    }

    public Optional<ActiveToken> getActive(Integer userId, LocalDateTime now) {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("get_active_email_verification_token");
        query.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(2, LocalDateTime.class, ParameterMode.IN);
        query.setParameter(1, userId);
        query.setParameter(2, now);

        List<?> rows = query.getResultList();
        if (rows.isEmpty()) {
            return Optional.empty();
        }
        Object[] r = (Object[]) rows.getFirst();
        return Optional.of(new ActiveToken(
                ((Number) r[0]).intValue(),
                (String) r[1],
                ((Number) r[2]).intValue()));
    }

    public boolean consumeAttempt(Integer tokenId, int maxAttempts) {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("consume_token_attempt");
        query.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(2, Integer.class, ParameterMode.IN);
        query.setParameter(1, tokenId);
        query.setParameter(2, maxAttempts);
        return firstScalarAsLong(query.getResultList()) > 0;
    }

    public boolean markUsed(Integer tokenId, LocalDateTime now) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("mark_token_used");
        query.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(2, LocalDateTime.class, ParameterMode.IN);
        query.setParameter(1, tokenId);
        query.setParameter(2, now);
        return firstScalarAsLong(query.getResultList()) > 0;
    }

    public SendStats getSendStats(Integer userId, LocalDateTime hourAgo, LocalDateTime cooldownAgo) {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("get_email_verification_send_stats");
        query.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(2, LocalDateTime.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(3, LocalDateTime.class, ParameterMode.IN);
        query.setParameter(1, userId);
        query.setParameter(2, hourAgo);
        query.setParameter(3, cooldownAgo);

        List<?> rows = query.getResultList();
        if (rows.isEmpty()) {
            return new SendStats(0, 0);
        }
        Object[] r = (Object[]) rows.getFirst();
        return new SendStats(((Number) r[0]).longValue(), ((Number) r[1]).longValue());
    }

    private long firstScalarAsLong(List<?> rows) {
        if (rows.isEmpty()) {
            return 0;
        }
        Object first = rows.getFirst();
        if (first instanceof Object[] arr) {
            first = arr[0];
        }
        return ((Number) first).longValue();
    }
}
