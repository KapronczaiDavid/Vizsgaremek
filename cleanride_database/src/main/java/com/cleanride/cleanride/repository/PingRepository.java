package com.cleanride.cleanride.repository;
import com.cleanride.cleanride.entity.Ping;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PingRepository extends JpaRepository<Ping, Long> { }