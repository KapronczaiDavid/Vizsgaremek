package com.cleanride.cleanride.controller;

import com.cleanride.cleanride.dto.request.CreateVehicleRequest;
import com.cleanride.cleanride.dto.request.UpdateVehicleRequest;
import com.cleanride.cleanride.dto.response.VehicleResponse;
import com.cleanride.cleanride.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VehicleController {
    private final VehicleService vehicleService;

    @PostMapping("/users/{userId}/vehicles")
    public ResponseEntity<VehicleResponse> createVehicle(@PathVariable Integer userId,
                                                         @Valid @RequestBody CreateVehicleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vehicleService.createVehicle(userId, request));
    }

    @GetMapping("/vehicles/{id}")
    public VehicleResponse getVehicleById (@PathVariable Integer id) {return vehicleService.getVehicleById(id);
    }
    @GetMapping("/users/{userId}/vehicles")
    public List<VehicleResponse> getVehiclesByUser(@PathVariable Integer userId) {
        return vehicleService.getVehiclesByUser(userId);
    }
    @PutMapping("/vehicles/{id}")
    public VehicleResponse updateVehicle(@PathVariable Integer id,
                                         @Valid @RequestBody UpdateVehicleRequest request) {
        return vehicleService.updateVehicle(id, request);
    }
    @DeleteMapping("/vehicles/{id}")
    public void deleteVehicle(@PathVariable Integer id) {
        vehicleService.deleteVehicle(id);
    }
}