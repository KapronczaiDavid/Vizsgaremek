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
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthTokenService authTokenService;
    private final LoginAttemptService loginAttemptService;
    @Transactional
    public AdminResponse login(LoginRequest request, String clientIp) {
        String attemptKey = "admin|" + clientIp + "|" + request.getEmail().toLowerCase(Locale.ROOT);
        loginAttemptService.checkAllowed(attemptKey);

        Admin admin = adminRepository.getAdminByEmail(request.getEmail()).orElse(null);
        if (admin == null || !passwordEncoder.matches(request.getPassword(), admin.getPasswordHash())) {
            loginAttemptService.recordFailure(attemptKey);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Hibás email vagy jelszó");
        }
        loginAttemptService.reset(attemptKey);
        String token = jwtService.generateToken(admin.getId(), "ADMIN");
        authTokenService.saveSession(token, null, admin.getId());
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

