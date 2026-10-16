package com.cleanride.cleanride.repository;

import com.cleanride.cleanride.entity.Services;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository extends JpaRepository<Services, Integer> {

    boolean existsByName(String name);

    @Query(value = "CALL create_service(:name, :description, :price, :durationMinutes)", nativeQuery = true)
    Services createService(@Param("name") String name,
                           @Param("description") String description,
                           @Param("price") Integer price,
                           @Param("durationMinutes") Integer durationMinutes);

    @Query(value = "CALL get_all_services()", nativeQuery = true)
    List<Services> getAllServices();

    @Query(value = "CALL get_service_by_id(:id)", nativeQuery = true)
    Optional<Services> getServiceById(@Param("id") Integer id);

    @Modifying(clearAutomatically = true)
    @Query(value = "CALL update_service(:id,:name,:description, :price, :durationMinutes)", nativeQuery = true)
    void updateService(@Param("id") Integer id, @Param("name") String name,
                    @Param("description") String description, @Param("price") Integer price ,@Param("durationMinutes") Integer durationMinutes);

    @Modifying(clearAutomatically = true)
    @Query(value = "CALL deactivate_service(:id)", nativeQuery = true)
    void deactivateService(@Param("id") Integer id);

}