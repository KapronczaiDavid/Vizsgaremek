package com.cleanride.cleanride.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    @NotBlank(message = "név megadása kötelező")
    @Size(max = 50)
    private String name;

    @NotBlank(message = "az email cím megadása kötelező")
    @Email(message = "Az email nem érvényes")
    private String email;

    @NotBlank(message = "telefonszám megadása kötelező")
    @Size(max = 20)
    private String phone;

    @NotBlank(message = "jelszó megadása kötelező")
    @Size(min = 6, message = "a jelszó legalább 6 karakter legyen")
    private String password;
}