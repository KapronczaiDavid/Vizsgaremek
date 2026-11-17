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
                        "Nincs ilyen jármű id: " + id));
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
    public VehicleResponse updateVehicle(Integer id, Integer callerId, UpdateVehicleRequest r) {
        Vehicle vehicle = findVehicle(id);

        if (!vehicle.getUserId().equals(callerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ez nem a te járműved");
        }

        if (!Objects.equals(vehicle.getLicensePlate(), r.getLicensePlate())
                && r.getLicensePlate() != null
                && vehicleRepository.existsByLicensePlate(r.getLicensePlate())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ez a rendszám már használatban van: " + r.getLicensePlate());
        }

        vehicleRepository.updateVehicle(id, r.getBrand(), r.getLicensePlate(), r.getVehicleType());
        return getVehicleById(id);
    }


    @Transactional
    public void deleteVehicle(Integer id, Integer callerId) {
        Vehicle vehicle = findVehicle(id);

        if (!vehicle.getUserId().equals(callerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ez nem a te járműved");
        }

        vehicleRepository.deleteVehicle(id);
    }
}
