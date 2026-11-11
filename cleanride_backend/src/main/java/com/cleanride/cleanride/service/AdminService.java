package com.cleanride.cleanride.service;

import com.cleanride.cleanride.dto.request.LoginRequest;
import com.cleanride.cleanride.dto.response.AdminResponse;
import com.cleanride.cleanride.entity.Admin;
import com.cleanride.cleanride.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    @Transactional
    public AdminResponse login(LoginRequest request) {
        Admin admin = adminRepository.getAdminByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Hibás email vagy jelszó"));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Hibás email vagy jelszó");
        }

        return toResponseDto(admin);
    }
    private AdminResponse toResponseDto(Admin admin) {
        return AdminResponse.builder()
                .id(admin.getId())
                .name(admin.getName())
                .email(admin.getEmail())
                .phone(admin.getPhone())
                .build();
    }
}

