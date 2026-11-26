package com.cleanride.cleanride.dto.request;

import com.cleanride.cleanride.validation.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {

    @NotBlank(message = "az email cím megadása kötelező")
    @Email(message = "Az email nem érvényes")
    private String email;

    @NotBlank(message = "a kód megadása kötelező")
    @Pattern(regexp = "\\d{6}", message = "a kód 6 számjegyből áll")
    private String code;

    @NotBlank(message = "az új jelszó megadása kötelező")
    @StrongPassword
    private String newPassword;
}
