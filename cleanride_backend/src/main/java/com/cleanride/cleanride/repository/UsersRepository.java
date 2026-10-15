package com.cleanride.cleanride.repository;

import com.cleanride.cleanride.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Integer> {

    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    @Query("SELECT u FROM Users u WHERE u.isDeleted = false")
    List<Users> findAllActive();

    @Query("SELECT u FROM Users u WHERE u.id = :id AND u.isDeleted = false")
    Optional<Users> findActiveById(@Param("id") Integer id);

    @Query("SELECT u FROM Users u WHERE u.email = :email AND u.isDeleted = false")
    Optional<Users> findActiveByEmail(@Param("email") String email);
}