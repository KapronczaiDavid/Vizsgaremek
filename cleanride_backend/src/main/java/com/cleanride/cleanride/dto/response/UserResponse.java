package com.cleanride.cleanride.dto.response;

import com.cleanride.cleanride.entity.enums.LoyaltyLevel;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserResponse {
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private LoyaltyLevel loyaltyLevel;
}