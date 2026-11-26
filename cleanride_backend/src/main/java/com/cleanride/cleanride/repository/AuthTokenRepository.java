package com.cleanride.cleanride.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AuthTokenRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void createToken(Integer userId, Integer adminId,
                            String tokenHash, LocalDateTime expiresAt) {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("create_auth_token");
        query.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(2, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(3, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(4, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(5, LocalDateTime.class, ParameterMode.IN);

        query.setParameter(1, userId);
        query.setParameter(2, adminId);
        query.setParameter(3, "session");
        query.setParameter(4, tokenHash);
        query.setParameter(5, expiresAt);

        query.execute();
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
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("mark_token_used");
        query.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(2, LocalDateTime.class, ParameterMode.IN);
        query.setParameter(1, tokenId);
        query.setParameter(2, now);
        return firstScalarAsLong(query.getResultList()) > 0;
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

    public boolean existsValidForLogin(String tokenHash, LocalDateTime now) {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("exists_valid_auth_token_for_login");
        query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(2, LocalDateTime.class, ParameterMode.IN);
        query.setParameter(1, tokenHash);
        query.setParameter(2, now);

        List<?> result = query.getResultList();
        return !result.isEmpty();
    }

    public void revokeSession(String tokenHash, LocalDateTime now) {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("revoke_session");
        query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(2, LocalDateTime.class, ParameterMode.IN);
        query.setParameter(1, tokenHash);
        query.setParameter(2, now);
        query.execute();
    }

    public void revokeAllSessions(Integer userId, LocalDateTime now) {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("revoke_user_sessions");
        query.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(2, LocalDateTime.class, ParameterMode.IN);
        query.setParameter(1, userId);
        query.setParameter(2, now);
        query.execute();
    }

    public void purgeExpired(LocalDateTime cutoff) {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("purge_expired_tokens");
        query.registerStoredProcedureParameter(1, LocalDateTime.class, ParameterMode.IN);
        query.setParameter(1, cutoff);
        query.execute();
    }
}
