package com.cleanride.cleanride.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResendVerificationRequest {

    @NotBlank(message = "az email cím megadása kötelező")
    @Email(message = "Az email nem érvényes")
    private String email;
}
