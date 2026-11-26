package com.cleanride.cleanride.dto.request;

import com.cleanride.cleanride.entity.enums.VehicleBrand;
import com.cleanride.cleanride.entity.enums.VehicleType;
import com.cleanride.cleanride.validation.LicensePlate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVehicleRequest {
    @NotNull(message = "a márka megadása kötelező")
    private VehicleBrand brand;

    @NotBlank(message = "a rendszám megadása kötelező")
    @LicensePlate
    private String licensePlate;

    @NotNull(message = "az autó kivitelének megadása kötelező")
    private VehicleType vehicleType;
}

