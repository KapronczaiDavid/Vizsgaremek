package com.cleanride.cleanride.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;private String name;private String password;private String phone;
    private LocalDateTime deletedAt;
    private LocalDateTime updatedAt;private Integer loyaltyPoints;
    @Column(unique = true)
    private String email;
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}