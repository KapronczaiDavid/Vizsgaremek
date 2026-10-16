package com.cleanride.cleanride.service;

import com.cleanride.cleanride.dto.request.CreateServiceRequest;
import com.cleanride.cleanride.dto.request.UpdateServiceRequest;
import com.cleanride.cleanride.dto.response.ServiceResponse;
import com.cleanride.cleanride.entity.Services;
import com.cleanride.cleanride.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;

    @Transactional
    public ServiceResponse createService(CreateServiceRequest request) {
        if (serviceRepository.existsByName(request.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ez a szolgáltatás név már foglalt: " + request.getName());
        }

        Services saved = serviceRepository.createService(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getDurationMinutes());

        return toResponseDto(saved);
    }
    @Transactional
    public ServiceResponse updateService(Integer id, UpdateServiceRequest request) {
        Services service = findActiveService(id);

        if (!service.getName().equals(request.getName())
                && serviceRepository.existsByName(request.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ez a szolgáltatás név már foglalt: " + request.getName());
        }
        serviceRepository.updateService(id, request.getName(), request.getDescription(), request.getPrice(),request.getDurationMinutes());
        return getServiceById(id);
    }

    @Transactional
    public void deactivateService(Integer id) {
        findActiveService(id);
        serviceRepository.deactivateService(id);
    }

    public List<ServiceResponse> getAll() {
        return serviceRepository.getAllServices().stream()
                .map(this::toResponseDto)
                .toList();
    }
    public ServiceResponse getServiceById(Integer id) {
        return toResponseDto(findActiveService(id));
    }

    private Services findActiveService(Integer id) {
        return serviceRepository.getServiceById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nincs ilyen szolgáltatás: " + id));
    }
    private ServiceResponse toResponseDto(Services service) {
        return ServiceResponse.builder()
                .id(service.getId())
                .name(service.getName())
                .description(service.getDescription())
                .price(service.getPrice())
                .durationMinutes(service.getDurationMinutes())
                .active(service.getActive())
                .build();
    }
}

