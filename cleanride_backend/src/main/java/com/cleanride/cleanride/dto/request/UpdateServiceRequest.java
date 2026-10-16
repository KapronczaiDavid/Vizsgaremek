package com.cleanride.cleanride.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class UpdateServiceRequest {
    @NotBlank(message = "név megadása kötelező")
    @Size(max = 100)
    private String name;


    private String description;

    @NotNull(message = "ár megadása kötelező")
    @Min(value = 1, message = "az ár legalább 1 legyen")
    private Integer price;

    @NotNull(message = "átlagos becsült idö megadása kötelező")
    @Min(value = 1, message = "az időtartam legalább 1 perc legyen")
    private Integer durationMinutes;

    @NotNull(message = "az aktív állapot megadása kötelező")
    private Boolean active;
}
