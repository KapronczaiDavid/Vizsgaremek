package com.cleanride.cleanride.repository;

import com.cleanride.cleanride.entity.Admin;
import com.cleanride.cleanride.entity.Users;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class AdminRepository {
    private final EntityManager entityManager;

    public boolean existsByEmail(String email) {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("exists_by_admin_email");

        query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
        query.setParameter(1, email);

        List<Object> result = query.getResultList();
        return ((Number) result.getFirst()).longValue() > 0;
    }
    public Optional<Admin> getAdminByEmail(String email) {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("get_admin_by_email", Admin.class);
        query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
        query.setParameter(1, email);
        List<Admin> result = query.getResultList();
        return result.stream().findFirst();
    }
}
