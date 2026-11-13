package com.cleanride.cleanride.repository;

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
public class    UsersRepository {

    private final EntityManager entityManager;

    public boolean existsByEmail(String email) {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("exists_by_user_email", Users.class);
        query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
        query.setParameter(1, email);
        List<Users> result = query.getResultList();
        return result.stream().findFirst().isPresent();
    }
    public boolean existsByPhone(String phone) {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("exists_by_user_phone", Users.class);
        query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
        query.setParameter(1, phone);
        List<Users> result = query.getResultList();
        return result.stream().findFirst().isPresent();
    }

    public Users createUser(String name, String email,
                            String phone, String password) {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("create_user", Users.class);

        query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(2, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(3, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(4, String.class, ParameterMode.IN);

        query.setParameter(1, name);
        query.setParameter(2, email);
        query.setParameter(3, phone);
        query.setParameter(4, password);

        List<Users> result = query.getResultList();
        return result.getFirst();
    }

    public List<Users> getAllUsers() {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("get_all_users", Users.class);
        return query.getResultList();
    }

    public Optional<Users> getUserById(Integer id) {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("get_user_by_id", Users.class);
        query.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        query.setParameter(1, id);
        List<Users> result = query.getResultList();
        return result.stream().findFirst();
    }

    public Optional<Users> getUserByEmail(String email) {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("get_user_by_email", Users.class);
        query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
        query.setParameter(1, email);
        List<Users> result = query.getResultList();
        return result.stream().findFirst();
    }

    public void updateUser(Integer id, String name, String email, String phone) {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("update_user");

        query.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(2, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(3, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(4, String.class, ParameterMode.IN);

        query.setParameter(1, id);
        query.setParameter(2, name);
        query.setParameter(3, email);
        query.setParameter(4, phone);

        query.execute();
        entityManager.clear();
    }

    public void deleteUser(Integer id) {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("delete_user");

        query.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        query.setParameter(1, id);

        query.execute();
        entityManager.clear();
    }
}