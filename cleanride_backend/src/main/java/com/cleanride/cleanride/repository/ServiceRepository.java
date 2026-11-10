package com.cleanride.cleanride.repository;

import com.cleanride.cleanride.entity.Services;
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
public class ServiceRepository{

    private final EntityManager entityManager;

    public boolean existsByName(String name) {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("exists_service_by_name");

        query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
        query.setParameter(1, name);

        List<Object> result = query.getResultList();
        return ((Number) result.getFirst()).longValue() > 0;
    }


    public Services createService(String name, String description,
                                  Integer price, Integer durationMinutes) {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("create_service", Services.class);

        query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(2, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(3, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(4, Integer.class, ParameterMode.IN);

        query.setParameter(1, name);
        query.setParameter(2, description);
        query.setParameter(3, price);
        query.setParameter(4, durationMinutes);

        List<Services> result = query.getResultList();
        return result.getFirst();
    }

    public List<Services> getAllServices() {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("get_all_services", Services.class);
        return query.getResultList();
    }
    public List<Services> getAllActiveServices() {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("get_all_active_services", Services.class);
        return query.getResultList();
    }
    public Optional<Services> getServiceById(Integer id) {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("get_service_by_id", Services.class);
        query.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        query.setParameter(1, id);
        List<Services> result = query.getResultList();
        return result.stream().findFirst();
    }
    public Optional<Services> getServiceByIdAll(Integer id) {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("get_service_by_id_all", Services.class);
        query.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        query.setParameter(1, id);
        List<Services> result = query.getResultList();
        return result.stream().findFirst();
    }

    public void updateService(Integer id,String name, String description,
                                  Integer price, Integer durationMinutes) {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("update_service");

        query.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(2, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(3, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(4, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(5, Integer.class, ParameterMode.IN);

        query.setParameter(1, id);
        query.setParameter(2, name);
        query.setParameter(3, description);
        query.setParameter(4, price);
        query.setParameter(5, durationMinutes);

        query.execute();
        entityManager.clear();
    }
    public void deactivateService(Integer id) {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("deactivate_service");

        query.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        query.setParameter(1, id);

        query.execute();
        entityManager.clear();
    }
    public void reactivateService(Integer id) {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("reactivate_service");

        query.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        query.setParameter(1, id);

        query.execute();
        entityManager.clear();
    }
}