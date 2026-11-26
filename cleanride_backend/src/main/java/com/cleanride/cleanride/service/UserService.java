package com.cleanride.cleanride.service;

import com.cleanride.cleanride.dto.request.CreateUserRequest;
import com.cleanride.cleanride.dto.request.LoginRequest;
import com.cleanride.cleanride.dto.request.UpdateUserRequest;
import com.cleanride.cleanride.dto.response.UserResponse;
import com.cleanride.cleanride.entity.Users;
import com.cleanride.cleanride.repository.UsersRepository;
import com.cleanride.cleanride.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthTokenService authTokenService;
    private final EmailVerificationService emailVerificationService;
    private final LoginAttemptService loginAttemptService;

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (request.getPassword().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A jelszó legfeljebb 72 bájt hosszú lehet");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ez az email cím már foglalt: " + request.getEmail());
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ez a telefonszám már foglalt: " + request.getPhone());
        }

        Users saved = userRepository.createUser(
                request.getName(),
                request.getEmail(),
                request.getPhone(),
                passwordEncoder.encode(request.getPassword()));

        emailVerificationService.sendInitialCode(saved);

        return toResponseDto(saved);
    }

    public List<UserResponse> getAll() {
        return userRepository.getAllUsers().stream()
                .map(this::toResponseDto)
                .toList();
    }

    public UserResponse getById(Integer id) {
        return toResponseDto(findActiveUser(id));
    }

    public UserResponse login(LoginRequest request, String clientIp) {
        String attemptKey = "user|" + clientIp + "|" + request.getEmail().toLowerCase(Locale.ROOT);
        loginAttemptService.checkAllowed(attemptKey);

        Users user = userRepository.getUserByEmail(request.getEmail()).orElse(null);
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            loginAttemptService.recordFailure(attemptKey);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Hibás email vagy jelszó");
        }
        loginAttemptService.reset(attemptKey);
        if (!Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Az email cím még nincs megerősítve");
        }
        String token = jwtService.generateToken(user.getId(), "CUSTOMER");
        authTokenService.saveSession(token, user.getId(), null);
        return toResponseDto(user, token);
    }

    @Transactional
    public UserResponse updateUser(Integer id, UpdateUserRequest request) {
        Users user = findActiveUser(id);
        boolean emailChanged = !user.getEmail().equalsIgnoreCase(request.getEmail());

        if (emailChanged
                && userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ez az email cím már foglalt: " + request.getEmail());
        }
        if (!user.getPhone().equals(request.getPhone())
                && userRepository.existsByPhone(request.getPhone())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ez a telefonszám már foglalt: " + request.getPhone());
        }

        userRepository.updateUser(id, request.getName(), request.getEmail(), request.getPhone());
        Users updated = findActiveUser(id);
        if (emailChanged) {
            // the DB procedure reset email_verified; the new address must be confirmed again
            emailVerificationService.sendInitialCode(updated);
        }
        return toResponseDto(updated);
    }

    @Transactional
    public void softDelete(Integer id) {
        findActiveUser(id);
        userRepository.deleteUser(id);
    }

    private Users findActiveUser(Integer id) {
        return userRepository.getUserById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nincs ilyen felhasználó: " + id));
    }

    private UserResponse toResponseDto(Users user) {
        return toResponseDto(user, null);
    }

    private UserResponse toResponseDto(Users user, String token) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .emailVerified(user.getEmailVerified())
                .loyaltyLevel(user.getLoyaltyLevel())
                .token(token)
                .build();
    }
}
