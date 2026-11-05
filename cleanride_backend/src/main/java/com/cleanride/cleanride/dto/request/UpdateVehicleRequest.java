package com.cleanride.cleanride.dto.request;

import com.cleanride.cleanride.entity.enums.VehicleBrand;
import com.cleanride.cleanride.entity.enums.VehicleType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @Size(max = 20, message = "a rendszám legfeljebb 20 karakter lehet")
    private String licensePlate;

    @NotNull(message = "az autó kivitelének megadása kötelező")
    private VehicleType vehicleType;
}

