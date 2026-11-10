package com.cleanride.cleanride.repository;

import com.cleanride.cleanride.entity.Vehicle;
import com.cleanride.cleanride.entity.enums.VehicleBrand;
import com.cleanride.cleanride.entity.enums.VehicleType;
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

public class VehicleRepository {

    private final EntityManager entityManager;

    public boolean existsByLicensePlate(String licensePlate) {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("exists_by_licenseplate");

        query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
        query.setParameter(1, licensePlate);

        List<Object> result = query.getResultList();
        return ((Number) result.getFirst()).longValue() > 0;
    }
    public Vehicle createVehicle(Integer userId, VehicleBrand brand,
                                 String licensePlate, VehicleType vehicleType) {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("create_vehicle", Vehicle.class);

        query.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(2, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(3, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(4, String.class, ParameterMode.IN);

        query.setParameter(1, userId);
        query.setParameter(2, brand.name());
        query.setParameter(3, licensePlate);
        query.setParameter(4, vehicleType.name());

        List<Vehicle> result = query.getResultList();
        return result.getFirst();
    }
    public List<Vehicle> getVehiclesByUser(Integer userId) {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("get_vehicle_by_user", Vehicle.class);

        query.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        query.setParameter(1, userId);

        return query.getResultList();
    }
    public Optional<Vehicle> getVehicleById(Integer id) {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("get_vehicle_by_id", Vehicle.class);

        query.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        query.setParameter(1, id);

        List<Vehicle> result = query.getResultList();
        return result.stream().findFirst();
    }
    public void updateVehicle(Integer id, VehicleBrand brand,
                              String licensePlate, VehicleType vehicleType) {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("update_vehicle");

        query.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(2, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(3, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(4, String.class, ParameterMode.IN);

        query.setParameter(1, id);
        query.setParameter(2, brand.name());
        query.setParameter(3, licensePlate);
        query.setParameter(4, vehicleType.name());

        query.execute();
        entityManager.clear();
    }
    public void deleteVehicle(Integer id) {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("delete_vehicle");

        query.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        query.setParameter(1, id);

        query.execute();
        entityManager.clear();
    }
}
