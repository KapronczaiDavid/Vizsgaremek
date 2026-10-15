package com.cleanride.cleanride.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class LoginRequest {
    @NotBlank(message = "jelszó megadása kötelező")
    private String password;

    @NotBlank(message = "az email cím megadása kötelező")
    @Email(message = "Az email nem érvényes")
    private String email;

    }
