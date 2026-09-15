package com.cleanride.cleanride.repository;
import com.cleanride.cleanride.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> { }