package com.cleanride.cleanride.service;

import com.cleanride.cleanride.dto.request.CreateVehicleRequest;
import com.cleanride.cleanride.dto.request.UpdateVehicleRequest;
import com.cleanride.cleanride.dto.response.VehicleResponse;
import com.cleanride.cleanride.entity.Vehicle;
import com.cleanride.cleanride.repository.VehicleRepository;
import com.cleanride.cleanride.security.AuthPrincipal;
import com.cleanride.cleanride.util.LicensePlateNormalizer;
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

        String plate = LicensePlateNormalizer.normalize(request.getLicensePlate());

        if (plate != null && vehicleRepository.existsByLicensePlate(plate)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ez a rendszám már használatban van: " + plate);
        }

        Vehicle saved = vehicleRepository.createVehicle(
                userId,
                request.getBrand(),
                plate,
                request.getVehicleType());

        return toResponseDto(saved);
    }

    public List<VehicleResponse> getVehiclesByUser(Integer userId) {
        userService.getById(userId);

        return vehicleRepository.getVehiclesByUser(userId).stream()
                .map(this::toResponseDto)
                .toList();
    }

    /** Customers may only read their own vehicles; admins may read any. Non-owners get a 404. */
    public VehicleResponse getVehicleForCaller(Integer id, AuthPrincipal caller) {
        Vehicle vehicle = findVehicle(id);
        if (!caller.isAdmin() && !vehicle.getUserId().equals(caller.id())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Jármű nem található");
        }
        return toResponseDto(vehicle);
    }

    private VehicleResponse getVehicleById(Integer id) {
        return toResponseDto(findVehicle(id));
    }

    /**
     * Only the owning customer may change a vehicle. Admin and customer ids live in different
     * tables, so the role is checked together with the id. Anyone else gets a 404, which does
     * not reveal whether the vehicle exists.
     */
    private Vehicle findOwnedVehicle(Integer id, AuthPrincipal caller) {
        Vehicle vehicle = findVehicle(id);
        if (!caller.isCustomer() || !vehicle.getUserId().equals(caller.id())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Jármű nem található");
        }
        return vehicle;
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
    public VehicleResponse updateVehicle(Integer id, AuthPrincipal caller, UpdateVehicleRequest r) {
        Vehicle vehicle = findOwnedVehicle(id, caller);

        String newPlate = LicensePlateNormalizer.normalize(r.getLicensePlate());

        if (newPlate != null
                && !Objects.equals(vehicle.getLicensePlate(), newPlate)
                && vehicleRepository.existsByLicensePlate(newPlate)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ez a rendszám már használatban van: " + newPlate);
        }

        vehicleRepository.updateVehicle(id, r.getBrand(), newPlate, r.getVehicleType());
        return getVehicleById(id);
    }


    @Transactional
    public void deleteVehicle(Integer id, AuthPrincipal caller) {
        findOwnedVehicle(id, caller);
        vehicleRepository.deleteVehicle(id);
    }
}
