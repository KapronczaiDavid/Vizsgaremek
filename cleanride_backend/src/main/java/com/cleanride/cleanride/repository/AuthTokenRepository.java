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

    public void purgeExpired(LocalDateTime cutoff) {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("purge_expired_tokens");
        query.registerStoredProcedureParameter(1, LocalDateTime.class, ParameterMode.IN);
        query.setParameter(1, cutoff);
        query.execute();
    }
}
