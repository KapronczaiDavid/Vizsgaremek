package com.cleanride.cleanride.service;

import com.cleanride.cleanride.dto.request.LoginRequest;
import com.cleanride.cleanride.dto.response.AdminResponse;
import com.cleanride.cleanride.entity.Admin;
import com.cleanride.cleanride.repository.AdminRepository;
import com.cleanride.cleanride.security.JwtService;
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
    private final JwtService jwtService;
    @Transactional
    public AdminResponse login(LoginRequest request) {
        Admin admin = adminRepository.getAdminByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Hibás email vagy jelszó"));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Hibás email vagy jelszó");
        }
        String token = jwtService.generateToken(admin.getId(), "ADMIN");   // <-- add

        return toResponseDto(admin, token);
    }
    private AdminResponse toResponseDto(Admin admin, String token) {
        return AdminResponse.builder()
                .id(admin.getId())
                .name(admin.getName())
                .email(admin.getEmail())
                .phone(admin.getPhone())
                .token(token)
                .build();
    }
}

