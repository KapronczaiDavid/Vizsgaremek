package com.cleanride.cleanride.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ServiceResponse {
    private Integer id;
    private String name;
    private String description;
    private Integer price;
    private Integer durationMinutes;
    private boolean active;
}



