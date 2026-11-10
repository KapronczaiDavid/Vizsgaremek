package com.cleanride.cleanride.dto.response;

import com.cleanride.cleanride.entity.enums.VehicleBrand;
import com.cleanride.cleanride.entity.enums.VehicleType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class VehicleResponse {
    private Integer id;
    private VehicleBrand brand;
    private String licensePlate;
    private VehicleType vehicleType;
}