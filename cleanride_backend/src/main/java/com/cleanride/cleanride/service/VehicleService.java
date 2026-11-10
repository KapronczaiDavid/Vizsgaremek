package com.cleanride.cleanride.service;

import com.cleanride.cleanride.dto.request.CreateVehicleRequest;
import com.cleanride.cleanride.dto.request.UpdateVehicleRequest;
import com.cleanride.cleanride.dto.response.VehicleResponse;
import com.cleanride.cleanride.entity.Vehicle;
import com.cleanride.cleanride.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserService userService;

    @Transactional
    public VehicleResponse createVehicle(Integer userId, CreateVehicleRequest request) {
        userService.getById(userId);

        Vehicle saved = vehicleRepository.createVehicle(
                userId,
                request.getBrand(),
                request.getLicensePlate(),
                request.getVehicleType());

        return toResponseDto(saved);
    }

    public List<VehicleResponse> getVehiclesByUser(Integer userId) {
        userService.getById(userId);

        return vehicleRepository.getVehiclesByUser(userId).stream()
                .map(this::toResponseDto)
                .toList();
    }

    public VehicleResponse getVehicleById(Integer id) {
        return toResponseDto(findVehicle(id));
    }

    private Vehicle findVehicle(Integer id) {
        return vehicleRepository.getVehicleById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nincs ilyen jármű: " + id));
    }


    private VehicleResponse toResponseDto(Vehicle vehicle) {
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .brand(vehicle.getBrand())
                .licensePlate(vehicle.getLicensePlate())
                .vehicleType(vehicle.getVehicleType())
                .build();
    }

    @Transactional
    public VehicleResponse updateVehicle(Integer id, UpdateVehicleRequest request) {
        Vehicle vehicle = findVehicle(id);

        if (!Objects.equals(vehicle.getLicensePlate(), request.getLicensePlate())
                && request.getLicensePlate() != null
                && vehicleRepository.existsByLicensePlate(request.getLicensePlate())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ez a rendszám már használatban van: " + request.getLicensePlate());
        }

        vehicleRepository.updateVehicle(
                id,
                request.getBrand(),
                request.getLicensePlate(),
                request.getVehicleType());

        return getVehicleById(id);
    }


    @Transactional
    public void deleteVehicle(Integer id) {
        findVehicle(id);
        vehicleRepository.deleteVehicle(id);
    }
}
