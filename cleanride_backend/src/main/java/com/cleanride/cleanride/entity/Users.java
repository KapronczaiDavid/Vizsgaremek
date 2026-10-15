package com.cleanride.cleanride.entity;

import jakarta.persistence.*;
import lombok.*;
import com.cleanride.cleanride.entity.enums.LoyaltyLevel;

import java.time.LocalDateTime;
    @Entity
    @Table(name = "users")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class Users {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id")
        private Integer id;

        @Column(name = "name", nullable = false, length = 50)
        private String name;

        @Column(name = "email", nullable = false, length = 100, unique = true)
        private String email;

        @Column(name = "email_verified", nullable = false)
        @Builder.Default
        private Boolean emailVerified = false;

        @Column(name = "phone", nullable = false, length = 20, unique = true)
        private String phone;

        // A jelszót mindig BCrypt hash-ként tároljuk, soha nyers szövegként
        @Column(name = "password", nullable = false, length = 255)
        private String passwordHash;

        @Column(name = "xp_points", nullable = false)
        @Builder.Default
        private Integer xpPoints = 0;

        @Enumerated(EnumType.STRING)
        @Column(name = "loyalty_level", nullable = false)
        @Builder.Default
        private LoyaltyLevel loyaltyLevel = LoyaltyLevel.BRONZE;

        @Column(name = "is_deleted", nullable = false)
        @Builder.Default
        private Boolean isDeleted = false;

        @Column(name = "deleted_at")
        private LocalDateTime deletedAt;

        @Column(name = "created_at", insertable = false, updatable = false)
        private LocalDateTime createdAt;

        @Column(name = "updated_at", insertable = false, updatable = false)
        private LocalDateTime updatedAt;
    }