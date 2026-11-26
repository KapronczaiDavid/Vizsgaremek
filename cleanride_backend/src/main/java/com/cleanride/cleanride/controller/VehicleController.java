package com.cleanride.cleanride.controller;

import com.cleanride.cleanride.dto.request.CreateVehicleRequest;
import com.cleanride.cleanride.dto.request.UpdateVehicleRequest;
import com.cleanride.cleanride.dto.response.VehicleResponse;
import com.cleanride.cleanride.security.AuthPrincipal;
import com.cleanride.cleanride.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public VehicleResponse getVehicleById(@PathVariable Integer id,
                                          @AuthenticationPrincipal AuthPrincipal principal) {
        return vehicleService.getVehicleForCaller(id, principal);
    }

    @GetMapping("/me/vehicles")
    public List<VehicleResponse> getMyVehicles(@AuthenticationPrincipal AuthPrincipal principal) {
        return vehicleService.getVehiclesByUser(principal.id());
    }

    @PostMapping("/me/vehicles")
    public ResponseEntity<VehicleResponse> createMyVehicle(@AuthenticationPrincipal AuthPrincipal principal,
                                                           @Valid @RequestBody CreateVehicleRequest request) {
        VehicleResponse response = vehicleService.createVehicle(principal.id(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/users/{userId}/vehicles")
    public List<VehicleResponse> getVehiclesByUser(@PathVariable Integer userId) {
        return vehicleService.getVehiclesByUser(userId);
    }

    @PutMapping("/vehicles/{id}")
    public VehicleResponse updateVehicle(@PathVariable Integer id,
                                         @AuthenticationPrincipal AuthPrincipal principal,
                                         @Valid @RequestBody UpdateVehicleRequest request) {
        return vehicleService.updateVehicle(id, principal, request);
    }

    @DeleteMapping("/vehicles/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Integer id,
                                              @AuthenticationPrincipal AuthPrincipal principal) {
        vehicleService.deleteVehicle(id, principal);
        return ResponseEntity.noContent().build();
    }
}