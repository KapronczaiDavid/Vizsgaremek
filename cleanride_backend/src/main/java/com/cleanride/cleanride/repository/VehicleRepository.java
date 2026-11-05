package com.cleanride.cleanride.repository;


import com.cleanride.cleanride.entity.Users;
import com.cleanride.cleanride.entity.Vehicle;
import jakarta.persistence.Enumerated;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

    boolean existsByLicensePlate(String licensePlate);

    @Query(value = "CALL create_vehicle(:VehicleBrand, :licensePlate, :VehicleType)", nativeQuery = true)
    Vehicle createVehicle(@Param("VehicleBrand") Enumerated VehicleBrand,
                           @Param("licensePlate") String licensePlate,
                           @Param("VehicleType") Enumerated VehicleType);

    @Query(value = "CALL get_vehicle_by_user(:userId)", nativeQuery = true)
    List<Vehicle> getVehiclesByUser(@Param("userId") Integer userId);

    @Query(value = "CALL get_vehicle_by_id(:id)", nativeQuery = true)
    List<Vehicle> getVehiclesById(@Param("id") Integer Id);

    @Modifying(clearAutomatically = true)
    @Query(value = "CALL update_vehicle(:id, :VehicleBrand, :licensePlate, :VehicleType)", nativeQuery = true)
    void updateVehicle(@Param("id") Integer id, @Param("VehicleBrand") Enumerated VehicleBrand,
                    @Param("licensePlate") String licensePlate, @Param("VehicleType") Enumerated  VehicleType);

    @Modifying(clearAutomatically = true)
    @Query(value = "CALL delete_vehicle(:id)", nativeQuery = true)
    void deleteVehicle(@Param("id") Integer id);
}