package com.cleanride.cleanride.controller;

import com.cleanride.cleanride.dto.request.CreateServiceRequest;
import com.cleanride.cleanride.dto.request.UpdateServiceRequest;
import com.cleanride.cleanride.dto.response.ServiceResponse;
import com.cleanride.cleanride.service.ServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @PostMapping
    public ResponseEntity<ServiceResponse> createService(@Valid @RequestBody CreateServiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceService.createService(request));
    }

    @GetMapping
    public List<ServiceResponse> getAll() {
        return serviceService.getAll();
    }
    @GetMapping("/{id}")
    public ServiceResponse getServiceById (@PathVariable Integer id) {return serviceService.getServiceById(id);
    }
    @PutMapping("/{id}")
    public ServiceResponse updateService(@PathVariable Integer id,
                                   @Valid @RequestBody UpdateServiceRequest request) {
        return serviceService.updateService(id, request);

    }
    @DeleteMapping("/{id}")
    public void deactivateService(@PathVariable Integer id) {
        serviceService.deactivateService(id);
    }
}