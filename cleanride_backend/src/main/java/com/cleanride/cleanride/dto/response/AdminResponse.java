package com.cleanride.cleanride.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class AdminResponse {

    private Integer id;
    private String name;
    private String email;
    private String phone;
    private String token;
}
