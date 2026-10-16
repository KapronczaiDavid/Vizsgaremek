package com.cleanride.cleanride.repository;

import com.cleanride.cleanride.entity.Services;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Services, Integer> {

    boolean existsByName(String name);

    @Query(value = "CALL create_service(:name, :description, :price, :durationMinutes)", nativeQuery = true)
    Services createService(@Param("name") String name,
                           @Param("description") String description,
                           @Param("price") Integer price,
                           @Param("durationMinutes") Integer durationMinutes);

    @Query(value = "CALL get_all_services()", nativeQuery = true)
    List<Services> getAllServices();
}
